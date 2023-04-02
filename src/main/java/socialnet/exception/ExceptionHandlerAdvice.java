package socialnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import socialnet.api.ErrorRs;

import java.sql.Timestamp;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<ErrorRs> handleRegisterException(RegisterException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("RegisterException");
        errorRs.setErrorDescription(e.getLocalizedMessage());
        errorRs.setTimestamp(new Timestamp(System.currentTimeMillis()));

        return new ResponseEntity<>(errorRs, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorRs> handlePostException(RegisterException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("PostException");
        errorRs.setErrorDescription(e.getLocalizedMessage());
        errorRs.setTimestamp(new Timestamp(System.currentTimeMillis()));

        return new ResponseEntity<>(errorRs, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyEmailException.class)
    public ResponseEntity<ErrorRs> handEmptyEmailException(EmptyEmailException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("EmptyEmailException");
        errorRs.setErrorDescription(e.getLocalizedMessage());
        errorRs.setTimestamp(new Timestamp(System.currentTimeMillis()));

        return new ResponseEntity<>(errorRs, HttpStatus.BAD_REQUEST);
    }
}
