package ar.edu.utn.ba.ddsi.climalert.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WeatherApiConfig {

    @Value("${weatherapi.url-base}")
    private String baseUrl;

    @Bean
    public RestClient weatherApiClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
