package ar.edu.utn.ba.ddsi.climalert.models.entities.alerta;

import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaClimatica {

    private Long id;
    private LocalDateTime fechaGeneracion;
    private RegistroClima registroOrigen;
    private List<String> destinatarios;
    private String cuerpoMensaje;
    private boolean notificada;

    public static AlertaClimatica crearAlerta(RegistroClima registro, List<String> destinatarios, String cuerpoMensaje) {
        return AlertaClimatica.builder()
                .fechaGeneracion(LocalDateTime.now())
                .registroOrigen(registro)
                .destinatarios(destinatarios)
                .cuerpoMensaje(cuerpoMensaje)
                .notificada(false)
                .build();
    }

    public void marcarComoNotificada() {
        this.notificada = true;
    }
}
