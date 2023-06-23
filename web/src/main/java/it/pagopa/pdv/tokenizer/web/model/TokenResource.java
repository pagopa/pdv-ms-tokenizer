package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResource {

    @ApiModelProperty(value = "${swagger.model.token.token}", required = true)
    @NotNull
    private UUID token;
    @ApiModelProperty(value = "${swagger.model.token.rootToken}", required = true)
    @NotNull
    private UUID rootToken;

}
