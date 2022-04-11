package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class TokenResource {

    @ApiModelProperty(value = "${swagger.model.token.token}", required = true)
    @NotNull
    private UUID token;
    @ApiModelProperty(value = "${swagger.model.token.rootToken}")
    private UUID rootToken;

}
