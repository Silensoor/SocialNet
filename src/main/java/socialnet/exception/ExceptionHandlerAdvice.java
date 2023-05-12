package socialnet.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import socialnet.api.response.ErrorRs;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<ErrorRs> handleRegisterException(RegisterException e) {
        return ResponseEntity.badRequest().body(makeErrorRs("RegisterException", e));
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorRs> handlePostException(RegisterException e) {
        return ResponseEntity.badRequest().body(makeErrorRs("PostException", e));
    }

    @ExceptionHandler(EmptyEmailException.class)
    public ResponseEntity<ErrorRs> handleEmptyEmailException(EmptyEmailException e) {
        return ResponseEntity.badRequest().body(makeErrorRs("EmptyEmailException", e));
    }

    @ExceptionHandler(DialogsException.class)
    public ResponseEntity<ErrorRs> handleBadRequestException(DialogsException e) {
        return ResponseEntity.badRequest().body(makeErrorRs("Exception in API: /api/v1/dialogs", e));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorRs> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.badRequest().body(makeErrorRs("EntityNotFoundException", e));
    }

    private ErrorRs makeErrorRs(String error, Exception e) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError(error);
        errorRs.setErrorDescription(e.getMessage());
        errorRs.setTimestamp(System.currentTimeMillis());

        return errorRs;
    }

 }
