package container.restaurant.server.web;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.exceptioin.FailedAuthorizationException;
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException e) {
        return response(HttpStatus.BAD_REQUEST, e);
    }

    private ResponseEntity<EntityModel<ErrorDto>> response(HttpStatus status, Exception exception) {
        return ResponseEntity
                .status(status)
                .body(EntityModel.of(ErrorDto.from(exception)));
    }

    private ResponseEntity<EntityModel<ErrorDto>> response(
            HttpStatus status, ConstraintViolationException validException
    ) {
        return ResponseEntity
                .status(status)
                .body(EntityModel.of(ErrorDto.from(validException)));
    }

}
