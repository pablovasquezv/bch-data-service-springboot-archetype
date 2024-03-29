package cl.banchile.domain.model.command;

import cl.banchile.domain.model.enums.MaestroTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo del comando de dominio CrearMaestro
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y con argumentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearMaestroCommandModel {
    
    private String nombre;
    private String descripcion;
    private MaestroTipoEnum tipo;
}
