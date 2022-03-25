package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateTokenDto {

    @ApiModelProperty(value = "${swagger.tokenizer.token.model.pii}", required = true)
    private String pii;

}
