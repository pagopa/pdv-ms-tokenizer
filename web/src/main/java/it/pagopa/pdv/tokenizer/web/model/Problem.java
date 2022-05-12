package it.pagopa.pdv.tokenizer.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Problem implements Serializable {

    private String message;
    private String messageTitle;
    private String messageKey;
    private String errorCode;

    public Problem(String message) {
        this.message = message;
    }

}
