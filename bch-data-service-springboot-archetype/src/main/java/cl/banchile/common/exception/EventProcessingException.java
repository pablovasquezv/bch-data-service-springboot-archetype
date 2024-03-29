package cl.banchile.common.exception;

/**
 * Excepción a ser lanzada existe un error
 * en el procesamiento de un evento de integración
 */
public class EventProcessingException extends RuntimeException {

    public EventProcessingException(String message){
        super(message);
    }

    public EventProcessingException(String message, Throwable err){
        super(message, err);
    }
}
