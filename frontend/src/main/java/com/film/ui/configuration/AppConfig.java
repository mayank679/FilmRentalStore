package com.film.ui.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rt = new RestTemplate();
        // Prevent RestTemplate from throwing HttpClientErrorException on 4xx / 5xx.
        // Without this, any backend error (400 validation, 404 not found, 409 conflict)
        // crashes the frontend controller with an unhandled exception → whitelabel error page.
        rt.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false; // never treat any status as an error
            }
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // no-op — controllers check the boolean return from ApiService
            }
        });
        return rt;
    }
}
