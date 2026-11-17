package ar.edu.utn.dsi.bolsines.adapter;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** REST client for the DSI mock GPS service; API keys are sent as headers. */
@Component
public class ClienteGpsHttp {

    private final RestClient client;

    public ClienteGpsHttp(@Value("${tracker.base-url}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    public String get(String path, String apiKey, Map<String, String> params) {
        try {
            return client
                .get()
                .uri(builder -> {
                    builder.path(path);
                    params.forEach(builder::queryParam);
                    return builder.build();
                })
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(String.class);
        } catch (Exception e) {
            throw new GpsException("No fue posible obtener ubicación desde GPS " + path, e);
        }
    }
}
