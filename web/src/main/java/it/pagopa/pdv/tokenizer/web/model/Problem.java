package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(description = "A \"problem detail\" as a way to carry machine-readable details of errors (https://datatracker.ietf.org/doc/html/rfc7807)")
public class Problem implements Serializable {

    @ApiModelProperty(value = "A URL to a page with more details regarding the problem.")
    private String type;

    @ApiModelProperty(value = "Short human-readable summary of the problem.", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "The HTTP status code.", required = true, example = "500")
    @Min(100)
    @Max(599)
    private int status;

    @ApiModelProperty(value = "Human-readable description of this specific problem.")
    private String detail;

    @ApiModelProperty(value = "A URI that describes where the problem occurred.")
    private String instance;

    @ApiModelProperty(value = "A list of invalid parameters details.")
    private List<InvalidParam> invalidParams;


    public Problem(HttpStatus httpStatus, String detail, List<InvalidParam> invalidParams) {
        this(httpStatus, detail);
        this.invalidParams = invalidParams;
    }


    public Problem(HttpStatus httpStatus, String detail) {
        this(httpStatus);
        this.detail = detail;
    }


    public Problem(HttpStatus httpStatus) {
        this.title = httpStatus.getReasonPhrase();
        this.status = httpStatus.value();
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    }


    @Data
    @AllArgsConstructor
    public static class InvalidParam {

        @ApiModelProperty(value = "Invalid parameter name.", required = true)
        @NotBlank
        private String name;

        @ApiModelProperty(value = "Invalid parameter reason.", required = true)
        @NotBlank
        private String reason;

    }

}
