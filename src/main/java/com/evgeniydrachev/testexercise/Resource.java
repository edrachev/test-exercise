package com.evgeniydrachev.testexercise;

import com.evgeniydrachev.testexercise.log.Log;
import com.evgeniydrachev.testexercise.log.LogRepository;
import com.evgeniydrachev.testexercise.translation.Translator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class Resource {
    private final Translator translator;
    private final LogRepository logRepository;

    public Resource(Translator translator, LogRepository logRepository) {
        this.translator = translator;
        this.logRepository = logRepository;
    }

    @PostMapping("/translate")
    public Response translate(@RequestBody Request request, HttpServletRequest servletRequest) {
        logRepository.add(new Log(request, servletRequest.getRemoteAddr()));
        String translated = translator.translate(request.getText(), request.getFrom(), request.getTo());
        return new Response(translated);
    }

    @GetMapping("/logs")
    public List<Log> logs() {
        return logRepository.readAll();
    }
}
