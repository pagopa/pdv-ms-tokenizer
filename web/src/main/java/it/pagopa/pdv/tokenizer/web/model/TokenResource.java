package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class TokenResource {

    @ApiModelProperty(value = "${swagger.tokenizer.token.model.token}", required = true)
    private UUID token;
    @ApiModelProperty(value = "${swagger.tokenizer.token.model.rootToken}")
    private UUID rootToken;

}
