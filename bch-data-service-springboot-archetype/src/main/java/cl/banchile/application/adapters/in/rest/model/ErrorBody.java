package cl.banchile.application.adapters.in.rest.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase modelo con el cuerpo del mensaje de error como respuesta globar a una excepción de un flujo proveniente de Rest Adapter
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y sin argumentos
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorBody {

    private Integer code;
    private String message;
    private String detailedMessage;
    private LocalDateTime timestamp;

}
