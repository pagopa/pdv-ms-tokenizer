package it.pagopa.pdv.tokenizer.web.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DummyModel {

    @NotNull
    String value;

}
