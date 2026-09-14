package vn.edu.hcmute.khanggearver2.web.api;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.hcmute.khanggearver2.exception.BusinessRuleException;
import vn.edu.hcmute.khanggearver2.exception.ResourceNotFoundException;

@RestControllerAdvice(basePackages = "vn.edu.hcmute.khanggearver2.web.api")
@Profile("!foundation")
public class ApiErrorHandler {
    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<ApiError> business(BusinessRuleException ex) { return ResponseEntity.badRequest().body(new ApiError(false, ex.getMessage())); }
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(false, ex.getMessage())); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> invalid(IllegalArgumentException ex) { return ResponseEntity.badRequest().body(new ApiError(false, "Dữ liệu gửi lên không hợp lệ.")); }
    record ApiError(boolean success, String message) {}
}
