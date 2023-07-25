package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class TokenResource {

    @Schema(description = "${swagger.model.token.token}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private UUID token;
    @Schema(description = "${swagger.model.token.rootToken}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private UUID rootToken;

}
