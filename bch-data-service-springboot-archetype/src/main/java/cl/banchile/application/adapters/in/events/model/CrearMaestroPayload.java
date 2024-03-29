package cl.banchile.application.adapters.in.events.model;

import cl.banchile.domain.model.enums.MaestroTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo del evento crear maestro
 * payload del evento de integración contiene la información requerida para tratar en comando 
 * CrearMaestro
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y con argumentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearMaestroPayload{
    private String nombre;
    private String descripcion;
    private MaestroTipoEnum tipo;
}
