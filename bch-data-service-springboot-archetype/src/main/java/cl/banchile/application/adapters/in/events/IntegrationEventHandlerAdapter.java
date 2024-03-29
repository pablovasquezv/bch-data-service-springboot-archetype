package cl.banchile.application.adapters.in.events;

import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cl.banchile.application.adapters.in.events.model.CrearDetallePayload;
import cl.banchile.application.adapters.in.events.model.CrearMaestroPayload;
import cl.banchile.application.common.model.EventModel;
import cl.banchile.common.enums.IntegrationEventTypeEnum;
import cl.banchile.common.exception.EventRejectedException;
import cl.banchile.domain.model.command.CrearDetalleCommandModel;
import cl.banchile.domain.model.command.CrearMaestroCommandModel;
import cl.banchile.domain.ports.in.DomainCommandPort;
import lombok.extern.slf4j.Slf4j;

/**
 * Constructor de Handler de eventos de integración
 * entre servicios perteneciendes a un contexto
 */
@Component
@Slf4j
public class IntegrationEventHandlerAdapter {


    //Mapper para parseo de objetos JSON
    //consistente son formato utilizado en Rest Controller
    private ObjectMapper objectMapper;

    private String contextName;
    private String domainName;
    private DomainCommandPort domainCommandPort;

    /**
     * Constructor de Handler de eventos de integración
     * entre servicios perteneciendes a un contexto
     * @param contextName nombre del contexto del servicio
     * @param domainName nombre del dominio del servicio
     * @param objectMapper inyección de ObjectMapper para parsing de 
     * @param domainCommandPort puerto de comandos como punto de entrada al dominio
     */
    public IntegrationEventHandlerAdapter(
        @Value("${archetype.context-name}") String contextName,
        @Value("${archetype.domain-name}") String domainName,
        @Autowired ObjectMapper objectMapper,
        @Autowired DomainCommandPort domainCommandPort
    ){
        this.contextName = contextName;
        this.domainName = domainName;
        this.objectMapper = objectMapper;
        this.domainCommandPort = domainCommandPort;
    }




    /**
     * Bean que hace bind con la configuración, por nombre.
     * Se filtran los mensajes que tengan como origen dominio propio
     * @return Consumer
    */
    @Bean
    public Consumer<Message<EventModel>> integrationEventsConsumer(){
        return value -> {
            value.getHeaders().entrySet().stream()
                .filter( entry -> "amqp_receivedRoutingKey".equals(entry.getKey()))
                .filter( entry -> !entry.getValue().toString().matches(this.contextName + "\\." + this.domainName + "\\.[a-z-\\*]+"))
                .findAny()
                .map( entry -> value)
                .ifPresent(this::processMessage);
        };
    }

    // Funcion privada para procesar el mensaje entrante
    private void processMessage(Message<EventModel> value){
        EventModel event = value.getPayload();
        IntegrationEventTypeEnum iet = IntegrationEventTypeEnum.of(event.getEventType());
        try{
            // Switch debe ser implementado con los eventos soportados por el dominio
            switch(iet){
                //Caso en que el evento de integración no sea reconocido por el dominio, solo se loguea
                case UNRECOGNIZED : {
                    log.info("Evento de integración no tratado por el dominio: {}",IntegrationEventTypeEnum.UNRECOGNIZED);
                    break;
                }
                //Caso de tipo de evento CREAR_MAESTRO
                case CREAR_MAESTRO : {
                    crearMaestroHandler(objectMapper.readValue(event.getPayload(), CrearMaestroPayload.class));
                    break;
                }
                //Caso tipo de evento CREAR_DETALLE
                case CREAR_DETALLE : {
                    crearDetalleHandler(objectMapper.readValue(event.getPayload(), CrearDetallePayload.class));
                    break;
                }
                // En el caso que se reconozca el evento como evento de integración de contexto
                //pero que no se trate, por lo que es omitido
                default : {
                    log.info("Evento de integración reconocido pero no tratado: {}", iet);
                    break;
                }
            }
        }
        catch(JsonProcessingException e){
            log.debug("Error al procesar el Evento de Integración",e);
            throw new EventRejectedException(e);
        }
    }

    /**
     * procedimiento asíncrono para manejo del
     * evento de integración CrearMaestro
     * @param crearMaestro payload del evento
     */
    @Async
    private void crearMaestroHandler(CrearMaestroPayload crearMaestro){
        log.info("Procesando comando {}", IntegrationEventTypeEnum.CREAR_MAESTRO);

        //Lamada a servicio de dominio crearMaestro
        domainCommandPort.crearMaestro(
            CrearMaestroCommandModel.builder()
                .nombre(crearMaestro.getNombre())
                .descripcion(crearMaestro.getDescripcion())
                .tipo(crearMaestro.getTipo())
            .build()
        );
    }

    /**
     * procedimiento asíncrono para manejo del
     * evento de integración CrearDetalle
     * @param crearDetalle payload del evento
     */
    @Async
    private void crearDetalleHandler(CrearDetallePayload crearDetalle){
        log.info("Procesando comando {}", IntegrationEventTypeEnum.CREAR_DETALLE);

        //LLamada a servicio de dominio crearDetalle
        domainCommandPort.crearDetalle(
            CrearDetalleCommandModel.builder()
                .idMaestro(crearDetalle.getIdMaestro())
                .cantidad(crearDetalle.getCantidad())
                .fecha(crearDetalle.getFecha())
                .monto(crearDetalle.getMonto())
            .build()
        );
    }
}
