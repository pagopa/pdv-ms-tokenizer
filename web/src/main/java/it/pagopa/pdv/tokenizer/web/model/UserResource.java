package it.pagopa.pdv.tokenizer.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class UserResource {

    @JsonProperty(required = true)
    private String internalId;
    @JsonProperty(required = true)
    private String externalId;
    @JsonProperty(required = true)
    private String certification;
    private Map<String, Object> cFields;
    private Map<String, Object> hcFields;

}
