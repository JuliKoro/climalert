package ar.edu.utn.ba.ddsi.climalert.services.impl;

import ar.edu.utn.ba.ddsi.climalert.models.entities.alerta.AlertaClimatica;
import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;
import ar.edu.utn.ba.ddsi.climalert.repositories.AlertaRepository;
import ar.edu.utn.ba.ddsi.climalert.repositories.RegistroClimaRepository;
import ar.edu.utn.ba.ddsi.climalert.services.NotificacionService;
import ar.edu.utn.ba.ddsi.climalert.services.ProveedorClima;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoreoClimaServiceImplTest {

    @Mock
    private RegistroClimaRepository registroClimaRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private ProveedorClima proveedorClima;

    @Mock
    private NotificacionService notificacionService;

    private List<String> destinatarios;
    private MonitoreoClimaServiceImpl monitoreoClimaService;

    @BeforeEach
    void setUp() {
        destinatarios = List.of("admin@clima.com", "emergencies@clima.com", "meteorologia@clima.com");
        monitoreoClimaService = new MonitoreoClimaServiceImpl(
                registroClimaRepository,
                alertaRepository,
                proveedorClima,
                notificacionService,
                destinatarios
        );
    }

    @Test
    void testRegistroClimaSuperaUmbralCritico() {
        // Temp > 35 y Hum > 60
        RegistroClima critico = RegistroClima.builder().temperatura(36.0).humedad(65.0).build();
        assertTrue(critico.superaUmbralCritico());

        // Solo temp alta
        RegistroClima soloTemp = RegistroClima.builder().temperatura(36.0).humedad(50.0).build();
        assertFalse(soloTemp.superaUmbralCritico());

        // Solo hum alta
        RegistroClima soloHum = RegistroClima.builder().temperatura(30.0).humedad(70.0).build();
        assertFalse(soloHum.superaUmbralCritico());

        // Ninguno
        RegistroClima normal = RegistroClima.builder().temperatura(25.0).humedad(40.0).build();
        assertFalse(normal.superaUmbralCritico());
    }

    @Test
    void testRegistrarClimaActualExitoso() {
        String ubicacion = "CABA";
        RegistroClima mockupClima = RegistroClima.builder()
                .temperatura(20.0)
                .humedad(50.0)
                .ubicacion(ubicacion)
                .condicion("Nublado")
                .analizado(false)
                .build();

        when(proveedorClima.obtenerClimaActual(ubicacion)).thenReturn(mockupClima);
        when(registroClimaRepository.save(mockupClima)).thenAnswer(invocation -> {
            RegistroClima arg = invocation.getArgument(0);
            arg.setId(1L);
            return arg;
        });

        RegistroClima guardado = monitoreoClimaService.registrarClimaActual(ubicacion);

        assertNotNull(guardado.getId());
        assertEquals(20.0, guardado.getTemperatura());
        assertEquals("Nublado", guardado.getCondicion());
        verify(proveedorClima, times(1)).obtenerClimaActual(ubicacion);
        verify(registroClimaRepository, times(1)).save(mockupClima);
    }

    @Test
    void testEvaluarAlertasCuandoEsCriticoYNoAnalizado() {
        RegistroClima criticoNoAnalizado = RegistroClima.builder()
                .id(1L)
                .fechaMedicion(LocalDateTime.now())
                .temperatura(38.0)
                .humedad(80.0)
                .ubicacion("CABA")
                .condicion("Caluroso")
                .analizado(false)
                .build();

        when(registroClimaRepository.findLatest()).thenReturn(Optional.of(criticoNoAnalizado));
        when(alertaRepository.save(any(AlertaClimatica.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(registroClimaRepository.save(any(RegistroClima.class))).thenAnswer(invocation -> invocation.getArgument(0));

        monitoreoClimaService.evaluarAlertas();

        // Verificar que el registro se marcó como analizado
        assertTrue(criticoNoAnalizado.isAnalizado());
        verify(registroClimaRepository, times(1)).save(criticoNoAnalizado);

        // Verificar que se envió la notificación
        ArgumentCaptor<AlertaClimatica> alertaCaptor = ArgumentCaptor.forClass(AlertaClimatica.class);
        verify(notificacionService, times(1)).enviarAlerta(alertaCaptor.capture());
        AlertaClimatica alertaEnviada = alertaCaptor.getValue();

        assertNotNull(alertaEnviada);
        assertTrue(alertaEnviada.isNotificada());
        assertEquals(destinatarios, alertaEnviada.getDestinatarios());
        assertEquals(criticoNoAnalizado, alertaEnviada.getRegistroOrigen());
        verify(alertaRepository, times(1)).save(alertaEnviada);
    }

    @Test
    void testEvaluarAlertasCuandoNoEsCriticoYNoAnalizado() {
        RegistroClima normalNoAnalizado = RegistroClima.builder()
                .id(1L)
                .fechaMedicion(LocalDateTime.now())
                .temperatura(20.0)
                .humedad(50.0)
                .ubicacion("CABA")
                .condicion("Nublado")
                .analizado(false)
                .build();

        when(registroClimaRepository.findLatest()).thenReturn(Optional.of(normalNoAnalizado));
        when(registroClimaRepository.save(any(RegistroClima.class))).thenAnswer(invocation -> invocation.getArgument(0));

        monitoreoClimaService.evaluarAlertas();

        // Debe marcarse como analizado
        assertTrue(normalNoAnalizado.isAnalizado());
        verify(registroClimaRepository, times(1)).save(normalNoAnalizado);

        // No debe generar alertas ni guardarlas
        verify(notificacionService, never()).enviarAlerta(any());
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void testEvaluarAlertasCuandoYaEstaAnalizado() {
        RegistroClima criticoAnalizado = RegistroClima.builder()
                .id(1L)
                .fechaMedicion(LocalDateTime.now())
                .temperatura(38.0)
                .humedad(80.0)
                .ubicacion("CABA")
                .condicion("Caluroso")
                .analizado(true)
                .build();

        when(registroClimaRepository.findLatest()).thenReturn(Optional.of(criticoAnalizado));

        monitoreoClimaService.evaluarAlertas();

        // No se debe guardar nada de nuevo ni mandar notificaciones, ya que ya se procesó
        verify(registroClimaRepository, never()).save(any());
        verify(notificacionService, never()).enviarAlerta(any());
        verify(alertaRepository, never()).save(any());
    }
}
