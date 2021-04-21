package container.restaurant.server.web.dto;

import lombok.Getter;

@Getter
public class ErrorDto {

    private final String errorType;

    private final String message;

    public ErrorDto(Exception e) {
        this.errorType = e.getClass().getSimpleName();
        this.message = getMessage();
    }

}
