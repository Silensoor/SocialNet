package socialnet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import socialnet.api.response.ErrorRs;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<ErrorRs> handleRegisterException(RegisterException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("RegisterException");
        errorRs.setErrorDescription(e.getMessage());
        errorRs.setTimestamp(System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorRs);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorRs> handlePostException(RegisterException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("PostException");
        errorRs.setErrorDescription(e.getMessage());
        errorRs.setTimestamp(System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorRs);
    }

    @ExceptionHandler(EmptyEmailException.class)
    public ResponseEntity<ErrorRs> handleEmptyEmailException(EmptyEmailException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("EmptyEmailException");
        errorRs.setErrorDescription(e.getMessage());
        errorRs.setTimestamp(System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorRs);
    }

    @ExceptionHandler(DialogsException.class)
    public ResponseEntity<ErrorRs> handleBadRequestException(DialogsException e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError("Exception in API: /api/v1/dialogs");
        errorRs.setErrorDescription(e.getMessage());
        errorRs.setTimestamp(System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorRs);
    }
}
