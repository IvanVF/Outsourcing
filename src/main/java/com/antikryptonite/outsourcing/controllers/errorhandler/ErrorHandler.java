package com.antikryptonite.outsourcing.controllers.errorhandler;

import com.antikryptonite.outsourcing.controllers.enums.ErrorMessages;
import com.antikryptonite.outsourcing.dto.ExceptionResponse;
import com.antikryptonite.outsourcing.exceptions.ResourceNotFoundException;
import com.antikryptonite.outsourcing.exceptions.ServerErrorException;
import com.antikryptonite.outsourcing.exceptions.UniqueException;
import com.antikryptonite.outsourcing.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * Обработчик исключений
 */
@ControllerAdvice
public class ErrorHandler {

    private static ResponseEntity<Object> generateDefaultExceptionResponse(String message, List<String> descriptionList, HttpStatus httpStatus) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(message);
        exceptionResponse.setDescriptionList(descriptionList);
        exceptionResponse.setHttpCode(httpStatus.value());
        return ResponseEntity.status(exceptionResponse.getHttpCode()).body(exceptionResponse);
    }

    private static final List<String> noDescriptionList = new ArrayList<>();

    @ExceptionHandler({ServerErrorException.class})
    private static ResponseEntity<Object> handleServerError() {
        return generateDefaultExceptionResponse("Error on server", noDescriptionList, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({UniqueException.class})
    private static ResponseEntity<Object> handleUnique(UniqueException exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add(exception.getMessage());
        return generateDefaultExceptionResponse("Field is not unique", descriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    private static ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add("Method name: " + exception.getMethod());
        return generateDefaultExceptionResponse("Not supported method", descriptionList, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    private static ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add("Type: " + exception.getContentType().toString());
        return generateDefaultExceptionResponse("Not supported media type", descriptionList, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    private static ResponseEntity<Object> handleHttpMediaTypeNotAcceptable() {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add("Request handler cannot generate a response that is acceptable by the client");
        return generateDefaultExceptionResponse("Not acceptable", descriptionList, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({MissingPathVariableException.class})
    private static ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add("Variable name: " + exception.getVariableName());
        return generateDefaultExceptionResponse("Path variable is needed", descriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    private static ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add("Parameter name: " + exception.getParameterName() + " Parameter type: " + exception.getParameterType());
        return generateDefaultExceptionResponse("Parameter is missing", descriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    private static ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        List<String> descriptionList = new ArrayList<>();
        String ex = exception.getMessage();
        descriptionList.add("Maybe wrong type? Exception: " + ex.split(";")[0]);
        return generateDefaultExceptionResponse("Server can not read the http message", descriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    private static ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<String> descriptionList = new ArrayList<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            descriptionList.add(error.getField() + " " + error.getDefaultMessage());
        }
        for (ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            descriptionList.add(" " + error.getDefaultMessage());
        }

        return generateDefaultExceptionResponse("Validation fails", descriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    private static ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> descriptionList = new ArrayList<>();
        for (ConstraintViolation<?> error : exception.getConstraintViolations()) {
            descriptionList.add(error.getMessage());
        }

        return generateDefaultExceptionResponse("Validation fails", descriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    private static ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add(exception.getMessage());
        return generateDefaultExceptionResponse("Resource not found", descriptionList, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({Exception.class})
    private static ResponseEntity<Object> handleOther(Exception exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add(exception.getMessage());
        return generateDefaultExceptionResponse(
                "Some server error has happened", /*List.of(Arrays.toString(exception.getStackTrace()))*/descriptionList, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    private static ResponseEntity<Object> handleMethodArgumentTypeMismatchException() {
        return generateDefaultExceptionResponse("Argument type are wrong", noDescriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    private static ResponseEntity<Object> handleNoHandlerFound() {
        return generateDefaultExceptionResponse("The page is not found", noDescriptionList, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AuthWrongException.class})
    private static ResponseEntity<Object> handleAuthWrongException() {
        return generateDefaultExceptionResponse("Authentication error", noDescriptionList, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AccessDeniedException.class})
    private static ResponseEntity<Object> handleAccessDenied(AccessDeniedException exception) {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add(exception.getMessage());
        return generateDefaultExceptionResponse("Access Denied", descriptionList, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    private static ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException exception) {
        List<String> descriptionLst = new ArrayList<>();
        descriptionLst.add(exception.getMessage());
        return generateDefaultExceptionResponse("Illegal argument", descriptionLst, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FileUploadException.class})
    private static ResponseEntity<Object> handleFileUpload(FileUploadException exception) {
        return generateDefaultExceptionResponse(exception.getMessage(), noDescriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationFallsException.class})
    private static ResponseEntity<Object> handleValidationFallsException(ValidationFallsException exception) {
        return generateDefaultExceptionResponse(exception.getMessage(), noDescriptionList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NonConfirmRegistrationException.class})
    private static ResponseEntity<Object> handleNotConfirmRegistrationException() {
        List<String> descriptionList = new ArrayList<>();
        descriptionList.add(ErrorMessages.NO_CONFIRM_REGISTRATION.name());
        return generateDefaultExceptionResponse("No confirm registration", descriptionList, HttpStatus.BAD_REQUEST);
    }
}
