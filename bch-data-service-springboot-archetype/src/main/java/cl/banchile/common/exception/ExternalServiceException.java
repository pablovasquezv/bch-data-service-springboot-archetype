package cl.banchile.common.exception;

/**
 * Excepción a ser lanzada cuando se detecta 
 * un error en un llamado a un servicio externo
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message){
        super(message);
    }

    public ExternalServiceException(String message, Throwable err){
        super(message, err);
    }
}
