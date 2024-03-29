package cl.banchile.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cl.banchile.application.adapters.in.rest.RestControllerAdapter;
import cl.banchile.application.adapters.out.jpa.JpaDaoAdapter;
import cl.banchile.application.adapters.out.jpa.model.MaestroEntity;
import cl.banchile.application.adapters.out.jpa.provider.jpa.JPADetalleRepository;
import cl.banchile.application.adapters.out.jpa.provider.jpa.JPAMaestroRepository;
import cl.banchile.domain.model.command.CrearDetalleCommandModel;
import cl.banchile.domain.model.command.CrearMaestroCommandModel;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.model.enums.MaestroTipoEnum;
import cl.banchile.domain.model.to.CrearResourceResponse;
import cl.banchile.domain.ports.in.DomainCommandPort;
import cl.banchile.domain.ports.in.DomainQueryPort;
import cl.banchile.domain.ports.out.BoredApiPort;
import cl.banchile.domain.ports.out.DatabasePort;
import cl.banchile.domain.ports.out.IntegrationEventPublisherPort;
import cl.banchile.domain.service.DomainService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class ServicesTests {
	
	@Autowired
	private JPAMaestroRepository maestroRepository;
	
	@Autowired
	private JPADetalleRepository detalleRepository;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private RestControllerAdapter restControllerAdapter;


	/** 'TestMethodOrder' + 'Order' permiten ejecutar test en orden. Variable global se usa para pasar datos entre métodos de test */
	private Long generatedMaestroId = 0L;

	// Command & query drinving port al dominio
	private DomainCommandPort domainCommandDrivingPort;
	private DomainQueryPort domainQueryDrivingPort;

	/** Inicializando Services y adapters que contengan dependecias que requieran Mock, una sola vez para toda la Test Class */
	@BeforeAll
	private void setUp(){
		IntegrationEventPublisherPort integrationEventPublisherPort;
		DomainService domainService;
		DatabasePort databasePort;
		BoredApiPort boredApiPort;
		log.info("RUNNIG setUp");
		//Implementación del DAO por adaptador JPA
		databasePort = new JpaDaoAdapter(detalleRepository, maestroRepository);

		integrationEventPublisherPort = Mockito.mock(IntegrationEventPublisherPort.class);
		boredApiPort = Mockito.mock(BoredApiPort.class);
		domainService = new DomainService(databasePort, integrationEventPublisherPort ,boredApiPort);
		domainCommandDrivingPort = domainService;
		domainQueryDrivingPort = domainService;
	}

	@Test
	@Order(1)
	public void testMaestroServiceCrearMaestro(){
		log.info("RUNNIG testMaestroServiceCrearMaestro TEST");
		CrearResourceResponse crearMaestroResponse = 
		domainCommandDrivingPort.crearMaestro(
				CrearMaestroCommandModel.builder()
					.nombre("test nombre")
					.tipo(MaestroTipoEnum.TIPO4)
					.descripcion("test description")
				.build()
			);

		assertNotNull(crearMaestroResponse, "chequeando que respuesta a maestro creado no sea nulo");
		assertTrue(() -> crearMaestroResponse.getId() > 0, "Chequeando que el is autogenerado de maestro sea mayor a cero");

		//En base a @Order y variable estática, permite pasar datos a otros Test Methods
		generatedMaestroId = crearMaestroResponse.getId();
	}

	@Test
	@Order(2)
	public void testDetalleServiceCrearDetalle(){
		log.info("RUNNIG testDetalleServiceCrearDetalle TEST");
		MaestroEntity restoredMaestroEntity = maestroRepository.findById(generatedMaestroId).get();

		CrearResourceResponse crearDetalleResponse =
			domainCommandDrivingPort.crearDetalle(
				CrearDetalleCommandModel.builder()
					.idMaestro(restoredMaestroEntity.getId())
					.cantidad(10000)
					.monto(200000.001)
					.fecha(LocalDate.now())
				.build()
			);
		
		assertNotNull(crearDetalleResponse, "Chequeando que respuesta de creación de detalle no sea nulo");
		assertTrue(() -> crearDetalleResponse.getId() > 0, "Chequenado que el id autogenerado de detalle sea mayor a cero");
	}

	@Test
	@Order(3)
	public void testXXX(){
		log.info("RUNNIG testXXX TEST");
		MaestroModel maestroResponse = domainQueryDrivingPort.obtenerMaestro(generatedMaestroId);

		assertNotNull(maestroResponse, "Chequenado que se obtenga un maestro con el id especificado");
		assertEquals(maestroResponse.getId(), generatedMaestroId, "chequeando que el id del maestro retornado es el esperado");
		assertEquals(maestroResponse.getNombre(), "test nombre", "chequeando que el nombre corresponda al esperado");
		assertEquals(maestroResponse.getTipo(), MaestroTipoEnum.TIPO4, "Chequeando que el tipo maestro sea el esperado");
		assertEquals(maestroResponse.getDescripcion(), "test description", "Chequeando de descripción sea la esperada");
	}

}
