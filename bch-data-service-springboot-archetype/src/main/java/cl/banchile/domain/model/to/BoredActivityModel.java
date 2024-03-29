package cl.banchile.domain.model.to;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de dominio con la infomación proveniente
 * de Bored API como serviico externo
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y con argumentos
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoredActivityModel {
    private String activity;
    private String type;
    private Long participants;
    private Double price;
}
