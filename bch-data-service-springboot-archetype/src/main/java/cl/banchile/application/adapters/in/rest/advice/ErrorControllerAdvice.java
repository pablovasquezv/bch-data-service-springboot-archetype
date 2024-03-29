package cl.banchile.application.adapters.in.rest.advice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import cl.banchile.application.adapters.in.rest.model.ErrorBody;
import cl.banchile.common.exception.ExternalServiceException;
import cl.banchile.common.exception.ResourceConflictException;
import cl.banchile.common.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller Advice para control global de errores
 * relativos a flujos que nacen desde el Rest Controller
 */
@RestControllerAdvice
@Slf4j
public class ErrorControllerAdvice {
    private static final Integer LOG_TRACE_LIMIT = 50;

    /**
     * Handler de excepciones de validación uktilizando 
     * Bean Validation Framework (implementación hibernate)
     * @param ex excepción a ser manejada
     * @return response entity con el cuerpo del error
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach( error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Error de validación de argumento en contexto Web")
                .detailedMessage(errors.toString())
                .timestamp(LocalDateTime.now())
            .build()
            ,HttpStatus.BAD_REQUEST)
        ;

    }

    /**
     * Handler de excepciones de parseo implícito de argumentos desde rest controllers
     * Event handler y publisher
     * @param ex excepción
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(value= IllegalArgumentException.class)
    protected ResponseEntity<ErrorBody> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
            .build()
            ,HttpStatus.BAD_REQUEST)
        ;
    }
   
    /**
     * Handler de excepciones de parseo implícito de argumentos desde rest controllers
     * Event handler y publisher
     * @param ex exception
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(value= HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorBody> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
            .build()
            ,HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handler de excepciones de parseo implícito de argumentos desde rest controllers
     * @param ex exception
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(value= ServerWebInputException.class)
    protected ResponseEntity<ErrorBody> handleServerWebInputException(ServerWebInputException ex){
        return
         new ResponseEntity<>(ErrorBody.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Error en los parámetros de entrada")
                .detailedMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build()
                , HttpStatus.BAD_REQUEST
            );
    }

    /**
     * handler de excepción customizada que se levanta cuando no existe
     * el recurso requerido
     * @param ex excepción
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ErrorBody> handleResourceNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
            .build()
            ,HttpStatus.NOT_FOUND
        );
    }

    /**
     * handler de excepción customizada que se levanta cuando no existe
     * el recurso requerido
     * @param ex excepción
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(ResourceConflictException.class)
    protected ResponseEntity<ErrorBody> handleResourceConflictException(ResourceConflictException ex){
        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
            .build()
            ,HttpStatus.CONFLICT
        );
    }


    /**
     * handler de excepción customizada que se levanta cuando hay un error
     * en un servicio externo
     * @param ex excepción
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(ExternalServiceException.class)
    protected ResponseEntity<ErrorBody> handleExternalServiceException(ExternalServiceException ex){
        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(HttpStatus.BAD_GATEWAY.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
            .build()
            ,HttpStatus.BAD_GATEWAY
        );
    }

    /**
     * handler de excepción customizada que se levanta cuando hay un error
     * de la librería Feign
     * @param ex excepción
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<ErrorBody> handleFeignException(FeignException ex){
        log.error(ex.getMessage(),ex);
        HttpStatus httpErrorStatus = HttpStatus.BAD_GATEWAY;
        
        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(httpErrorStatus.value())
                .message("Error en Servicio Externo")
                .detailedMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
            .build()
            , httpErrorStatus
        );
    }

    /**
     * handler de excepción genérica
     * no tratada por los otros handles
     * @param ex excepción
     * @return response entity con formato estandarizado
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorBody> handleException(Exception ex){
        log.error("[handleThrowableException]", ex);
        
        String limitedStackTrace = Arrays.asList(ex.getStackTrace())
            .stream()
            .limit(LOG_TRACE_LIMIT)
            .map(Object::toString)
            .collect(Collectors.joining("\n"));

        return new ResponseEntity<>(
            ErrorBody.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .detailedMessage(limitedStackTrace)
                .timestamp(LocalDateTime.now())
            .build()
            ,HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
