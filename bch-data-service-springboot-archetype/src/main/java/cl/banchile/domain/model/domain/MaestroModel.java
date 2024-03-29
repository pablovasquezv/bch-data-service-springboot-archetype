package cl.banchile.domain.model.domain;

import cl.banchile.domain.model.enums.MaestroTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de la entidad
 * de dominio Maestro 
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y con argumentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaestroModel {
    private Long id;
    private String nombre;
    private String descripcion;
    private MaestroTipoEnum tipo;
}
