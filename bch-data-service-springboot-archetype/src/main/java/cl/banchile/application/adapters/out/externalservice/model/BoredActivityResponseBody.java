package cl.banchile.application.adapters.out.externalservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo del mensaje como respuesta
 * del servicio externo de bored API
 * Lombok para la omisión de código redundante
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y sin argumentos
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoredActivityResponseBody {
    private String activity;
    private String type;
    private Long participants;
    private Double price;
    private String url;
    private String key;
    private Integer accessibility;
}
