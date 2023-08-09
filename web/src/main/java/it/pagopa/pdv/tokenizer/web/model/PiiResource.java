package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PiiResource {

    @Schema(description = "${swagger.model.token.pii}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String pii;

}
