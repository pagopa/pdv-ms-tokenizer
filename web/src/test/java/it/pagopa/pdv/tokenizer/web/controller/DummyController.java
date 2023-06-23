package it.pagopa.pdv.tokenizer.web.controller;

import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DummyController {

    public Mono<Object> notVoidMethodValidMonoResult() {
        return Mono.just(new DummyModel("valid"));
    }

    public Mono<Object> notVoidMethodInvalidMonoResult() {
        return Mono.just(new DummyModel());
    }

    public Mono<Void> voidMethodMono() {
        return Mono.empty();
    }

}
