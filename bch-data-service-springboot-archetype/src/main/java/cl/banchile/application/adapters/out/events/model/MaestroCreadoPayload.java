package cl.banchile.application.adapters.out.events.model;

import lombok.Builder;
import lombok.Value;

/**
 * payload del evento de integración CrearMaestro
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * setters con @Value
 */
@Value
@Builder
public class MaestroCreadoPayload {
    private Long id;
}
