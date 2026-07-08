package ar.edu.utn.ba.ddsi.climalert.schedulers;

import ar.edu.utn.ba.ddsi.climalert.services.MonitoreoClimaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClimaScheduler {

    private final MonitoreoClimaService monitoreoClimaService;
    private final String ubicacion;

    public ClimaScheduler(
            MonitoreoClimaService monitoreoClimaService,
            @Value("${weatherapi.ubicacion}") String ubicacion) {
        this.monitoreoClimaService = monitoreoClimaService;
        this.ubicacion = ubicacion;
    }

    // Tarea A: Consulta el clima actual y lo guarda. Se ejecuta cada 5 minutos (300.000 ms)
    @Scheduled(fixedRate = 300000)
    public void consultarYRegistrarClima() {
        log.info("[Scheduler - Tarea A] Iniciando consulta de clima periódica...");
        try {
            monitoreoClimaService.registrarClimaActual(ubicacion);
        } catch (Exception e) {
            log.error("[Scheduler - Tarea A] Falló la obtención del clima: {}", e.getMessage());
        }
    }

    // Tarea B: Analiza el último registro y emite alerta si es necesario. Se ejecuta cada 1 minuto (60.000 ms)
    @Scheduled(fixedRate = 60000)
    public void evaluarAlertasClimaticas() {
        log.info("[Scheduler - Tarea B] Iniciando análisis periódico de alertas...");
        try {
            monitoreoClimaService.evaluarAlertas();
        } catch (Exception e) {
            log.error("[Scheduler - Tarea B] Falló la evaluación de alertas: {}", e.getMessage());
        }
    }
}
