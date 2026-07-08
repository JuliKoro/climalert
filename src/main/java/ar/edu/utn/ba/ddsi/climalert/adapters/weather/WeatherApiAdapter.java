package ar.edu.utn.ba.ddsi.climalert.adapters.weather;

import ar.edu.utn.ba.ddsi.climalert.dtos.weatherapi.WeatherApiResponse;
import ar.edu.utn.ba.ddsi.climalert.exceptions.BusinessException;
import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;
import ar.edu.utn.ba.ddsi.climalert.services.ProveedorClima;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Component
public class WeatherApiAdapter implements ProveedorClima {

    private final RestClient restClient;
    private final String apiKey;

    public WeatherApiAdapter(RestClient restClient, @Value("${weatherapi.api-key}") String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    @Override
    public RegistroClima obtenerClimaActual(String ubicacion) {
        try {
            WeatherApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/current.json")
                            .queryParam("key", apiKey)
                            .queryParam("q", ubicacion)
                            .queryParam("aqi", "no")
                            .build())
                    .retrieve()
                    .body(WeatherApiResponse.class);

            if (response == null || response.current() == null || response.location() == null) {
                throw new BusinessException("No se recibieron datos válidos del proveedor de clima");
            }

            return RegistroClima.builder()
                    .fechaMedicion(LocalDateTime.now())
                    .temperatura(response.current().tempC())
                    .humedad(response.current().humidity())
                    .ubicacion(response.location().name())
                    .condicion(response.current().condition() != null ? response.current().condition().text() : "Desconocida")
                    .analizado(false)
                    .build();
        } catch (Exception e) {
            throw new BusinessException("Error al consultar el proveedor de clima: " + e.getMessage());
        }
    }
}
