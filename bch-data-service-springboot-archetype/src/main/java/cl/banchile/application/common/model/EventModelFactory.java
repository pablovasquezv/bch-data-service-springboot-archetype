package cl.banchile.application.common.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cl.banchile.common.enums.IntegrationEventTypeEnum;

/**
 * Factory del modelo de eventos genéricos
 */
@Component
public class EventModelFactory {
    
    private ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencia
     * @param objectMapper para parseo de estructuras JSON a objeto, mismo utilizado en Rest Controller para manteenr consistenca de formatos
     */
    public EventModelFactory(@Autowired ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    /**
     * Construye un modelo de evento genperico, en base al payload y el tipo de evento
     * @param <T> genérico a contruir
     * @param eventPayload payload a injertar en el evento de integración
     * @param eventType tipo del evento, debe ser reconodico por el dominio y el contexto
     * @return Modelo de evento genérico con el payload injertado
     * @throws JsonProcessingException cuando existe un error al parsear JSON
     */
    public <T> EventModel buildEventModel(T eventPayload, IntegrationEventTypeEnum eventType) throws JsonProcessingException{
        return new EventModel(
            UUID.randomUUID(),
            eventType.toString(),
            LocalDateTime.now(),
            objectMapper.writeValueAsString(eventPayload)
        );
    }
}
