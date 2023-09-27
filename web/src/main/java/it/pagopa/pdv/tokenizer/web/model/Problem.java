package it.pagopa.pdv.tokenizer.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "A \"problem detail\" as a way to carry machine-readable details of errors (https://datatracker.ietf.org/doc/html/rfc7807)")
public class Problem implements Serializable {

    @Schema(description = "A URL to a page with more details regarding the problem.")
    private String type;

    @Schema(description = "Short human-readable summary of the problem.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String title;

    @Schema(description = "The HTTP status code.", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    @Min(100)
    @Max(599)
    private int status;

    @Schema(description = "Human-readable description of this specific problem.")
    private String detail;

    @Schema(description = "A URI that describes where the problem occurred.")
    private String instance;

    @Schema(description = "A list of invalid parameters details.")
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
        if (requestAttributes != null && ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass())) {
            this.instance = ((ServletRequestAttributes) requestAttributes).getRequest().getRequestURI();
        }
    }


    @Data
    @AllArgsConstructor
    public static class InvalidParam {

        @Schema(description = "Invalid parameter name.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String name;

        @Schema(description = "Invalid parameter reason.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String reason;

    }

}
