package thanhcom.site.lkdt.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.responseApi.ResponseApi;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ---------- HANDLE @Valid trong @RequestBody ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseApi<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        HttpStatus responseStatus = ErrCode.USER_VALIDATION_FORMAT.getStatusCode();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String enumKey = error.getDefaultMessage();
            Map<String, Object> attributes = extractAttributes(error);
            ErrCode errCode = resolveErrCode(enumKey);
            String message = (errCode != null)
                    ? mapDynamicAttributes(errCode.getMessage(), attributes)
                    : "Invalid validation key: " + enumKey;

            fieldErrors.put(error.getField(), message);

            if (errCode != null) {
                responseStatus = errCode.getStatusCode(); // lấy status của field đầu tiên
            }
        }

        return buildValidationResponse(fieldErrors, responseStatus);
    }

    // ---------- HANDLE @Valid trong @RequestParam, @PathVariable, @RequestHeader ----------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseApi<?>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        HttpStatus responseStatus = ErrCode.USER_VALIDATION_FORMAT.getStatusCode();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String enumKey = violation.getMessage();
            Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
            ErrCode errCode = resolveErrCode(enumKey);
            String message = (errCode != null)
                    ? mapDynamicAttributes(errCode.getMessage(), attributes)
                    : "Invalid validation key: " + enumKey;

            String field = violation.getPropertyPath().toString();
            if (field.contains(".")) {
                field = field.substring(field.lastIndexOf('.') + 1);
            }
            fieldErrors.put(field, message);

            if (errCode != null) {
                responseStatus = errCode.getStatusCode();
            }
        }

        return buildValidationResponse(fieldErrors, responseStatus);
    }

    // ---------- HANDLE AppException ----------
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ResponseApi<?>> handleAppException(AppException ex) {
        ResponseApi<Void> response = new ResponseApi<>();
        response.setResponseCode(ex.getErrCode().getCode());
        response.setMessenger(ex.getMessage());
        return new ResponseEntity<>(response, ex.getErrCode().getStatusCode());
    }

    // =======================================================
    // 🧩 HELPER SECTION
    // =======================================================

    private ResponseEntity<ResponseApi<?>> buildValidationResponse(Map<String, String> fieldErrors, HttpStatus status) {
        ResponseApi<Map<String, String>> response = new ResponseApi<>();
        response.setResponseCode(ErrCode.USER_VALIDATION_FORMAT.getCode());
        response.setMessenger("Validation failed");
        response.setData(fieldErrors);
        return new ResponseEntity<>(response, status);
    }

    private ErrCode resolveErrCode(String enumKey) {
        try {
            return ErrCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Map<String, Object> extractAttributes(FieldError fieldError) {
        try {
            return fieldError.unwrap(ConstraintViolation.class)
                    .getConstraintDescriptor()
                    .getAttributes();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String mapDynamicAttributes(String message, Map<String, Object> attributes) {
        if (message == null || attributes == null || attributes.isEmpty()) return message;

        Pattern pattern = Pattern.compile("\\{(\\w+)}");
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = attributes.getOrDefault(key, "{" + key + "}");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(value)));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
