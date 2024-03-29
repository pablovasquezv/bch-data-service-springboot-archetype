package cl.banchile.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import cl.banchile.application.adapters.in.events.IntegrationEventHandlerAdapter;
import cl.banchile.application.adapters.in.events.model.CrearMaestroPayload;
import cl.banchile.application.adapters.out.events.IntegrationEventPublisherAdapter;
import cl.banchile.application.common.model.EventModelFactory;
import cl.banchile.common.enums.IntegrationEventTypeEnum;
import cl.banchile.common.exception.EventRejectedException;
import cl.banchile.domain.model.enums.MaestroTipoEnum;
import cl.banchile.domain.model.to.CrearResourceResponse;
import cl.banchile.domain.ports.in.DomainCommandPort;
import cl.banchile.domain.ports.in.DomainQueryPort;
import cl.banchile.tests.helper.CustomMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * pruebas de configuraciones
 * de Spring Cloud Stream
 * Se usa ActiveProfiles para realizar las pruebas en un profile aislado del resto de test classes por
 * inconsistencia en carga de configuración de ejecución de pruebas
 */
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("STREAM_TEST_PROFILE")
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class IntegrationEventsTests {

	@Autowired
	private InputDestination inputDestination;

	@Autowired
	private OutputDestination outputDestination;

	@Autowired
	private StreamBridge streamBridge;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${archetype.context-name}")
	private String contextName;

    @Value("${archetype.domain-name}")
	private String domainName;

	private IntegrationEventHandlerAdapter inputAdapter;
	private IntegrationEventPublisherAdapter outputAdapter;
	private EventModelFactory eventModelFactory;
	

	@MockBean
	private DomainCommandPort domainCommandPort;

	@MockBean
	private DomainQueryPort domainQueryPort;


	@BeforeAll
	private void setupTest(){
		eventModelFactory= new EventModelFactory(objectMapper);
		inputAdapter = new IntegrationEventHandlerAdapter(contextName,domainName, objectMapper, domainCommandPort);
		outputAdapter = new IntegrationEventPublisherAdapter(streamBridge, eventModelFactory);
	}

	@Test
	@Order(1)
	public void testEmptyConfiguration() {
		// Usando default charset para conversión segura
		byte[] encodedByteArray = Charset.defaultCharset().encode("HELLO").array();
		this.inputDestination.send(new GenericMessage<byte[]>(encodedByteArray));
		byte[] responseByteArray = outputDestination.receive(1000, "integrationEventsConsumer-out-0.destination").getPayload();
		log.info("*********** a:{} b:{} ",new String(encodedByteArray), new String(responseByteArray));
		assertArrayEquals(responseByteArray, encodedByteArray, "el payload es el esperado");
	}

	@SpringBootApplication
	@Import(TestChannelBinderConfiguration.class)
	@Profile("STREAM_TEST_PROFILE")
	public static class SampleConfiguration {

		@Profile("STREAM_TEST_PROFILE")
		@Bean
		public Function<String, String> integrationEventsConsumer() {
			return String::toUpperCase;
		}
	}

	@Test
	@Order(2)
	public void testIntegrationEventHandlerAdapter(){

		CustomMessage messageToBeRejected = new CustomMessage(
			"{mensaje debe generar excepción EventRejectedException al no poder parsearlo}",
			IntegrationEventTypeEnum.CREAR_MAESTRO.toString(),
			objectMapper,
			contextName
		);

		CustomMessage messageToBeProcessedMaestro = new CustomMessage(
			CrearMaestroPayload.builder()
				.descripcion("descripcion de prueba")
				.nombre("nombre de prueba")
				.tipo(MaestroTipoEnum.TIPO2)
			.build(),
			IntegrationEventTypeEnum.CREAR_MAESTRO.toString(),
			objectMapper,
			contextName
		);

		CustomMessage messageToBeProcessedDetalle = new CustomMessage(
			CrearMaestroPayload.builder()
				.descripcion("descripcion de prueba")
				.nombre("nombre de prueba")
				.tipo(MaestroTipoEnum.TIPO2)
			.build()
			, IntegrationEventTypeEnum.CREAR_DETALLE.toString(),
			objectMapper,
			contextName
		);

		CustomMessage messageNoReconocido = new CustomMessage("{mensaje no se reconoce}", "OTRO_EVENTO", objectMapper, contextName);

		CustomMessage customMessage = new CustomMessage("{evento conocido no se procesa}", "MAESTRO_CREADO", objectMapper, contextName);


		assertThrows(EventRejectedException.class, () -> inputAdapter.integrationEventsConsumer().accept(messageToBeRejected), "Se espera una excepción de parsing");
		assertDoesNotThrow(() -> inputAdapter.integrationEventsConsumer().accept(messageToBeProcessedMaestro), "Evento CrearMaestro se procesa");
		assertDoesNotThrow(() -> inputAdapter.integrationEventsConsumer().accept(messageToBeProcessedDetalle), "Evento CrearDetalle se procesa");
		assertDoesNotThrow(() -> inputAdapter.integrationEventsConsumer().accept(messageNoReconocido), "Evento no se reconoce");
		assertDoesNotThrow(() -> inputAdapter.integrationEventsConsumer().accept(customMessage), "Evento no se reconoce");

		
	}


	

	/*****************Publishers Tests*************************/

	/**
	 * Prueba de configuración de event publisher
	 */
	@Test
	@Order(3)
	public void testWithInterceptorsMatchedAgainstAllPatterns() {
			byte[] encodedByteArray = Charset.defaultCharset().encode("HELLO FOO").array();
			streamBridge.send("application.integrationEventsPublisher-out-0", "HELLO FOO");
			Message<byte[]> message = outputDestination.receive(100, "application.integrationEventsPublisher-out-0");
			assertArrayEquals(new String(message.getPayload()).getBytes(),encodedByteArray, "Enviado debe ser lo mismo en salida del canal");
	}

	@Test
	@Order(4)
	public void publishIntegrationEventTest(){
		assertDoesNotThrow(() -> 
			outputAdapter.publishMaestroCreadoEvent(
				CrearResourceResponse.builder()
					.id(1L)
					.fechaCreacion(LocalDateTime.now())
				.build()
			)
			, "publicación de evento conocido no lanza excepción"
		);
	}

}