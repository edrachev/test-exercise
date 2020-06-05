package com.evgeniydrachev.testexercise.translation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OneWordTranslator {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public OneWordTranslator() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String translate(String word, String from, String to) {
        try {
            URI uri = UriComponentsBuilder.newInstance()
                    .uri(URI.create("https://api.mymemory.translated.net/get"))
                    .queryParam("q", word)
                    .queryParam("langpair", from + "|" + to)
                    .build().toUri();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Response response = objectMapper.readValue(httpResponse.body(), Response.class);

            return response.responseData.translatedText;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Cannot get response from API", e);
        }
    }

    private static class Response {
        public Data responseData;
    }

    private static class Data {
        public String translatedText;
    }
}
