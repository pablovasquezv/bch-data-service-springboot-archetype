package cl.banchile.application.adapters.in.rest.model;

import cl.banchile.domain.model.enums.MaestroTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo del mensaje de respuesta a una solicitud de maestro desde rest Adapter
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y sin argumentos
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaestroResponseBody {
    private Long id;
    private String nombre;
    private String descripcion;
    private MaestroTipoEnum tipo;
}
