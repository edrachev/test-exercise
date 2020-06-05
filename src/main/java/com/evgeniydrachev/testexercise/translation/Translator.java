package com.evgeniydrachev.testexercise.translation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:application.properties")
public class Translator {

    private final OneWordTranslator oneWordTranslator;

    private final ExecutorService executorService;

    private static Function<Future<String>, String> extractValue = result -> {
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Cannot transalte word", e);
        }
    };

    public Translator(OneWordTranslator oneWordTranslator, @Value("${translator.threads}") String threads) {
        this.oneWordTranslator = oneWordTranslator;

        executorService = Executors.newFixedThreadPool(Integer.valueOf(threads));
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    public String translate(String text, String from, String to) {
        List<String> words = splitByWords(text);
        Function<String, Future<String>> translateOneWord = word -> executorService.submit(() -> oneWordTranslator.translate(word, from, to));

        List<Future<String>> futures = words.stream()
                .map(translateOneWord)
                .collect(Collectors.toList());

        List<String> translatedWords = futures.stream()
                .map(extractValue)
                .collect(Collectors.toList());

        return String.join(" ", translatedWords);
    }

    private List<String> splitByWords(String text) {
        return Arrays.asList(text.split(" "));
    }
}
