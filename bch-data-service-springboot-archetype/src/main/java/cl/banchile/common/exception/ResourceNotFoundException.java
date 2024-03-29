package cl.banchile.common.exception;

/**
 * Excepción a ser lanzada cuando se el recurso solicitado
 * no existe en el dominio
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable err){
        super(message, err);
    }
}
