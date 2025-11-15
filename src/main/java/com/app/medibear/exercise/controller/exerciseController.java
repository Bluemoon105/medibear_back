package com.app.medibear.exercise.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/exercise")
public class exerciseController {

    private final RestTemplate rest = new RestTemplate();

    // 이렇게 써도 되지만
//    @PostMapping("/analyze")

    // 제일 안전. json타입만 받는다 명시적 선언
    @PostMapping(value="/analyze",
            consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> analyze(@RequestBody String rawJson) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> req = new HttpEntity<>(rawJson, h);

        ResponseEntity<String> res =
                rest.postForEntity("http://localhost:5000/analyze", req, String.class);

        return ResponseEntity.status(res.getStatusCode())
                .headers(res.getHeaders())
                .body(res.getBody());

    }

}
