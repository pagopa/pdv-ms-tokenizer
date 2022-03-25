package it.pagopa.pdv.tokenizer.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserSearchDto {

    @ApiModelProperty(value = "${swagger.user-registry.users.model.externalId}", required = true)
    @JsonProperty(required = true)
    private String externalId;

}
