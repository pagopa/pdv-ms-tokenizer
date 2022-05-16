package it.pagopa.pdv.tokenizer.web.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DummyController {

    public Object notVoidMethodValidResult() {
        return List.of(new DummyModel("valid"));
    }

    public Object notVoidMethodInvalidResult() {
        return new DummyModel();
    }

    public void voidMethod() {
    }

}
