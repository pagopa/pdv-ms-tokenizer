package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateTokenDto {

    @ApiModelProperty(value = "${swagger.model.token.pii}", required = true)
    @NotBlank
    private String pii;

}
