package cl.banchile.common.exception;

/**
 * Excepción a ser lanzada cuando se rechaza un evento de integración
 */
public class EventRejectedException extends RuntimeException {

    public EventRejectedException(String message){
        super(message);
    }

    public EventRejectedException(String message, Throwable err){
        super(message, err);
    }

    public EventRejectedException(Throwable err){
        super(err);
    }
}