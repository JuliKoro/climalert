package ar.edu.utn.ba.ddsi.climalert.models.entities.clima;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistroClima {

    private Long id;
    private LocalDateTime fechaMedicion;
    private double temperatura;
    private double humedad;
    private String ubicacion;
    private String condicion;
    private boolean analizado;

    public boolean superaUmbralCritico() {
        return this.temperatura > 35.0 && this.humedad > 60.0;
    }

    public void marcarComoAnalizado() {
        this.analizado = true;
    }
}
