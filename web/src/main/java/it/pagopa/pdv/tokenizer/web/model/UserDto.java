package it.pagopa.pdv.tokenizer.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class UserDto {

    @ApiModelProperty(value = "${swagger.user-registry.users.model.externalId}", required = true)
    @JsonProperty(required = true)
    private String externalId;

    @ApiModelProperty(value = "${swagger.user-registry.users.model.certification}", required = true)
    @JsonProperty(required = true)
    private Certification certification;

    @ApiModelProperty(value = "${swagger.user-registry.users.model.certification}")
    private Map<String, Object> cFields;

    @ApiModelProperty(value = "${swagger.user-registry.users.model.certification}")
    private Map<String, Object> hcFields;

}
