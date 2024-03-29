package cl.banchile.domain.model.to;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response genérico a eventos de creación de recursos.
 * Se recomienda no modificar esta clase.
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor cin y con argumentos
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class CrearResourceResponse {
    private Long id;
    private LocalDateTime fechaCreacion;
}
