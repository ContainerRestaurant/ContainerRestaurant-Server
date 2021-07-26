package container.restaurant.server.exception;

import container.restaurant.server.web.dto.ErrorDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> notFoundException(ResourceNotFoundException e) {
        return response(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(FailedAuthorizationException.class)
    public ResponseEntity<?> authorizationException(FailedAuthorizationException e) {
        return response(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(UsingPushTokenException.class)
    public ResponseEntity<?> authorizationException(UsingPushTokenException e) {
        return response(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(EntityModel.of(ErrorDto.from(e)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> constraintViolationException(IllegalArgumentException e) {
        return response(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unauthorizedException(UnauthorizedException e) {
        return response(HttpStatus.UNAUTHORIZED, e);
    }

    private ResponseEntity<EntityModel<ErrorDto>> response(HttpStatus status, Exception exception) {
        return ResponseEntity
                .status(status)
                .body(EntityModel.of(ErrorDto.from(exception)));
    }

}
