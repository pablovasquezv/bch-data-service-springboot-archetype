package cl.banchile.domain.model.command;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo del comando de dominio
 * CrearDetalle.
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y con argumentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearDetalleCommandModel {
    private Long idMaestro;
    private LocalDate fecha;
    private Integer cantidad;
    private Double monto;
}
