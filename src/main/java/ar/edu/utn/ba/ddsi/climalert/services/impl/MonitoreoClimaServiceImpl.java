package ar.edu.utn.ba.ddsi.climalert.services.impl;

import ar.edu.utn.ba.ddsi.climalert.models.entities.alerta.AlertaClimatica;
import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;
import ar.edu.utn.ba.ddsi.climalert.repositories.AlertaRepository;
import ar.edu.utn.ba.ddsi.climalert.repositories.RegistroClimaRepository;
import ar.edu.utn.ba.ddsi.climalert.services.MonitoreoClimaService;
import ar.edu.utn.ba.ddsi.climalert.services.NotificacionService;
import ar.edu.utn.ba.ddsi.climalert.services.ProveedorClima;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MonitoreoClimaServiceImpl implements MonitoreoClimaService {

    private final RegistroClimaRepository registroClimaRepository;
    private final AlertaRepository alertaRepository;
    private final ProveedorClima proveedorClima;
    private final NotificacionService notificacionService;
    private final List<String> destinatarios;

    public MonitoreoClimaServiceImpl(
            RegistroClimaRepository registroClimaRepository,
            AlertaRepository alertaRepository,
            ProveedorClima proveedorClima,
            NotificacionService notificacionService,
            @Value("${climalert.destinatarios}") List<String> destinatarios) {
        this.registroClimaRepository = registroClimaRepository;
        this.alertaRepository = alertaRepository;
        this.proveedorClima = proveedorClima;
        this.notificacionService = notificacionService;
        this.destinatarios = destinatarios;
    }

    @Override
    public RegistroClima registrarClimaActual(String ubicacion) {
        log.info("Obteniendo clima actual para la ubicación: {}", ubicacion);
        RegistroClima nuevoRegistro = proveedorClima.obtenerClimaActual(ubicacion);
        RegistroClima guardado = registroClimaRepository.save(nuevoRegistro);
        log.info("Registro de clima guardado con ID: {} (Temp: {}°C, Hum: {}%)",
                guardado.getId(), guardado.getTemperatura(), guardado.getHumedad());
        return guardado;
    }

    @Override
    public void evaluarAlertas() {
        Optional<RegistroClima> ultimoRegistroOpt = registroClimaRepository.findLatest();

        if (ultimoRegistroOpt.isEmpty()) {
            log.debug("No hay registros de clima disponibles para evaluar.");
            return;
        }

        RegistroClima registro = ultimoRegistroOpt.get();

        if (registro.isAnalizado()) {
            log.debug("El último registro (ID: {}) ya fue analizado previamente.", registro.getId());
            return;
        }

        log.info("Analizando registro de clima ID: {}...", registro.getId());
        registro.marcarComoAnalizado();

        if (registro.superaUmbralCritico()) {
            log.warn("¡Condiciones críticas detectadas! Temperatura: {}°C, Humedad: {}%. Generando alerta...",
                    registro.getTemperatura(), registro.getHumedad());

            String cuerpoMensaje = armarCuerpoAlerta(registro);
            AlertaClimatica alerta = AlertaClimatica.crearAlerta(registro, destinatarios, cuerpoMensaje);

            try {
                notificacionService.enviarAlerta(alerta);
                alerta.marcarComoNotificada();
                log.info("Notificación de alerta enviada con éxito.");
            } catch (Exception e) {
                log.error("Error al enviar la notificación por correo: {}. La alerta se guardará como no notificada.",
                        e.getMessage());
            }

            alertaRepository.save(alerta);
        } else {
            log.info("El registro de clima ID: {} no supera los umbrales críticos de alerta.", registro.getId());
        }

        registroClimaRepository.save(registro);
    }

    @Override
    public List<RegistroClima> obtenerHistoricoClima() {
        return registroClimaRepository.findAll();
    }

    @Override
    public List<AlertaClimatica> obtenerHistoricoAlertas() {
        return alertaRepository.findAll();
    }

    private String armarCuerpoAlerta(RegistroClima registro) {
        return """
                --- ALERTA METEOROLÓGICA CRÍTICA ---
                Se han registrado condiciones climáticas peligrosas superando los umbrales de seguridad.
                
                Detalles de la medición:
                - Ubicación: %s
                - Fecha y Hora de Medición: %s
                - Temperatura actual: %.1f °C (Umbral máximo: 35.0 °C)
                - Humedad actual: %.1f %% (Umbral máximo: 60.0 %%)
                - Condición meteorológica: %s
                
                Por favor, tome las precauciones necesarias.
                ------------------------------------
                """.formatted(
                registro.getUbicacion(),
                registro.getFechaMedicion().toString(),
                registro.getTemperatura(),
                registro.getHumedad(),
                registro.getCondicion()
        );
    }
}
