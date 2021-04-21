package container.restaurant.server.web;

import container.restaurant.server.domain.exception.ResourceNotFoundException;
import container.restaurant.server.web.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public void notFoundException(EntityNotFoundException e) {
        ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(e));
    }

}
