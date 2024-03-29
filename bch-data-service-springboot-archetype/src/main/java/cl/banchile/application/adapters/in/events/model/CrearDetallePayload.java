package cl.banchile.application.adapters.in.events.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo del evento crear detalle
 * payload del evento de integración
 * contiene la información requerida para tratar en comando CrearDetalle
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y con argumentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearDetallePayload {
    private Long idMaestro;
    
    private LocalDate fecha;

    private Integer cantidad;

    private Double monto;
}
