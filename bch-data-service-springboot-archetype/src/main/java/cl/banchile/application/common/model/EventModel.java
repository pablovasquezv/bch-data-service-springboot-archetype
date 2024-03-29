package cl.banchile.application.common.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Modelo genérico de los eventos de dominio. Se recomienda no modificar esta clase para mantener la consistencia en la comunicación asíncrona
 * Lombok para la omisión de código redundante
 * Constructor con argumentos
 */
@Value
@AllArgsConstructor
public final class EventModel {
    private UUID eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String payload;
}
