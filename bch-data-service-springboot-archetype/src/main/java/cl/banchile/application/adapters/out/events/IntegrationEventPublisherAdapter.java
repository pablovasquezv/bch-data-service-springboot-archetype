package cl.banchile.application.adapters.out.events;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import cl.banchile.application.adapters.out.events.model.MaestroCreadoPayload;
import cl.banchile.application.common.model.EventModelFactory;
import cl.banchile.common.enums.IntegrationEventTypeEnum;
import cl.banchile.common.exception.EventProcessingException;
import cl.banchile.domain.model.to.CrearResourceResponse;
import cl.banchile.domain.ports.out.IntegrationEventPublisherPort;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de eventos de integración, entre los servicio del dominio
 */
@Component
@Slf4j
public class IntegrationEventPublisherAdapter implements IntegrationEventPublisherPort{

    // Canal de Cloud Stream de salida.
    private static final String STREAM_CHANNEL = "integrationEventsPublisher-out-0";

    // Permite enlazar implementación con channel publisher definido declarativamente en propiedades
	private StreamBridge streamBridge;

    // Permite la construcción de eventos con Modelo soportado por el contexto
    private EventModelFactory eventModelFactory;

    /**
     * Constructor con inyección de dependencia
     * @param streamBridge puente entre la impementación y la configuración de canale de salida definidos en Spring Cloud Stream
     * @param eventModelFactory factory de eventos de inmtegración
     */
    public IntegrationEventPublisherAdapter(
        @Autowired StreamBridge streamBridge,
        @Autowired EventModelFactory eventModelFactory
    ){
        this.streamBridge = streamBridge;
        this.eventModelFactory = eventModelFactory;
    }

    /**
     * publica un evento de integración MAESTRO_CREADO
     * a ser informado al resto de los servicios del contexto
     */
    public void publishMaestroCreadoEvent(CrearResourceResponse crearResourceResponse){
        log.info("publicando evento {}", IntegrationEventTypeEnum.MAESTRO_CREADO);

        try{
            if(!streamBridge.send(
                    STREAM_CHANNEL, 
                    eventModelFactory.buildEventModel(
                        MaestroCreadoPayload.builder()
                            .id(crearResourceResponse.getId())
                        .build()
                        , IntegrationEventTypeEnum.MAESTRO_CREADO
                    ) 
                )
            ){
                // Si no se puede enviar, send retona false. Lanzando excepción
                throw new EventProcessingException("No es posible publicar en evento de integración");
            }
        }
        catch(JsonProcessingException ex){
            log.error("Error al procesar mensaje de publicación",ex);
            throw new EventProcessingException("No es posible publicar en evento de integración", ex);
        }

    }
}
