package container.restaurant.server.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

    private final String errorType;

    private final List<String> messages;

    protected ErrorDto(Exception e) {
        this.errorType = e.getClass().getSimpleName();
        this.messages = List.of(e.getMessage());
    }

    protected ErrorDto(ConstraintViolationException e) {
        this.errorType = e.getClass().getSimpleName();
        this.messages = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    public static ErrorDto from(Exception e) {
        return new ErrorDto(e);
    }

    public static ErrorDto from(ConstraintViolationException e) {
        return new ErrorDto(e);
    }
}
