package faang.school.projectservice.controller.stage.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/stages")
public class StageExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<StageErrorResponse> handleNoSuchElementException (NoSuchElementException ex) {
        StageErrorResponse response = new StageErrorResponse(ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<> (response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<StageErrorResponse> handleIllegalArgumentException (IllegalArgumentException ex) {
        StageErrorResponse response = new StageErrorResponse(ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<> (response, HttpStatus.BAD_REQUEST);
    }
}
