package com.vibenotes.exception;

import com.vibenotes.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		logger.warn("Resource not found: {}", ex.getMessage());
		ErrorResponse error = new ErrorResponse("Resource not found", "The requested resource could not be found");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
		logger.warn("Unauthorized access attempt: {}", ex.getMessage());
		ErrorResponse error = new ErrorResponse("Access denied", "You do not have permission to access this resource");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
		logger.warn("Authentication failed: Invalid credentials");
		ErrorResponse error = new ErrorResponse("Invalid username or password", "Authentication failed");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
		logger.warn("Authentication error: {}", ex.getMessage());
		ErrorResponse error = new ErrorResponse("Authentication failed", "Please provide valid credentials");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<ErrorResponse> handleFileStorageException(FileStorageException ex) {
		logger.error("File storage error: {}", ex.getMessage(), ex);
		ErrorResponse error = new ErrorResponse("File operation failed", "An error occurred while processing the file");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
		logger.warn("File size exceeded: {}", ex.getMessage());
		ErrorResponse error = new ErrorResponse("File too large", "The uploaded file exceeds the maximum allowed size");
		return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		
		logger.warn("Validation error: {}", errors);
		ErrorResponse error = new ErrorResponse("Validation failed", "Please check your input and try again");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		logger.warn("Constraint violation: {}", ex.getMessage());
		ErrorResponse error = new ErrorResponse("Validation failed", "Invalid input provided");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		logger.warn("Invalid argument: {}", ex.getMessage());
		ErrorResponse error = new ErrorResponse("Invalid input", "The provided input is not valid");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		logger.error("Unexpected error occurred", ex);
		ErrorResponse error = new ErrorResponse("An error occurred", "Please try again later");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}

