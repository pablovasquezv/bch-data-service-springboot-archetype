package cl.banchile.domain.model.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de la entidad 
 * de dominio Detalle.
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y con argumentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleModel {
    private Long id;
    private LocalDate fecha;
    private Integer cantidad;
    private Double monto;
    private Long idMaestro;
}
