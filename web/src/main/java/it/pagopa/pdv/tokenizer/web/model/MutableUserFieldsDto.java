package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class MutableUserFieldsDto {

    @ApiModelProperty(value = "${swagger.user-registry.users.model.certification}")
    private Map<String, Object> cFields;

    @ApiModelProperty(value = "${swagger.user-registry.users.model.certification}")
    private Map<String, Object> hcFields;

}
