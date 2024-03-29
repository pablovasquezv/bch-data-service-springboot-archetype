package cl.banchile.tests.helper;

import java.util.Collections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import cl.banchile.application.common.model.EventModel;
import cl.banchile.application.common.model.EventModelFactory;
import cl.banchile.common.enums.IntegrationEventTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom message para testing
 * representa un tipo mensaje utitlizado por Spring Cloud Stream
 * para transferir mensajería
 */
@Slf4j
public class CustomMessage implements Message<EventModel>{

    private Object payload;
    private IntegrationEventTypeEnum eventTypeEnum;
    private EventModelFactory eventModelFactory;
    private String contextName;

    public CustomMessage(Object payload, String eventType, ObjectMapper objectMapper, String contextName){
        this.payload = payload;
        this.eventTypeEnum = IntegrationEventTypeEnum.of(eventType);
        eventModelFactory= new EventModelFactory(objectMapper);
    }
    
    @Override
    public MessageHeaders getHeaders() {
        return new MessageHeaders(Collections.singletonMap("amqp_receivedRoutingKey", contextName + ".otro-dominio.*"));
    }
    @Override
    public EventModel getPayload() {
        EventModel eventModel = null;
            try {
                eventModel = eventModelFactory.buildEventModel(
                    payload,
                    eventTypeEnum
                );
            } catch (JsonProcessingException e) {
                log.error("Error en la construcción del evento", e);
                e.printStackTrace();
            }
            return eventModel;
    }
}