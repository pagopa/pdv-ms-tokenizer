package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PiiResource {

    @Schema(description = "${swagger.model.token.pii}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String pii;

}
