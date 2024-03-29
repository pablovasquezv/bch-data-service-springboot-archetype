package cl.banchile.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ServerWebInputException;

import cl.banchile.application.adapters.in.rest.RestControllerAdapter;
import cl.banchile.application.adapters.in.rest.model.ErrorBody;
import cl.banchile.application.adapters.in.rest.model.MaestroResponseBody;
import cl.banchile.common.exception.ExternalServiceException;
import cl.banchile.common.exception.ResourceConflictException;
import cl.banchile.common.exception.ResourceNotFoundException;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.model.enums.MaestroTipoEnum;
import cl.banchile.domain.model.to.CrearResourceResponse;
import cl.banchile.domain.ports.in.DomainCommandPort;
import cl.banchile.domain.ports.in.DomainQueryPort;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebMvcTest(RestControllerAdapter.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_METHOD)
@TestMethodOrder(OrderAnnotation.class)
public class RestControllerAdapterTests {

    // Constates para evitar repetición de cadenas de caracteres
    private static final String MAESTROS_ENDPOINT = "/archetype/v1/maestros/";
    private static final String ADVICE_MESSAGE_TEST = "{\"nombre\": \"nombre de prueba\",\"descripcion\": \"descripcion de prueba\",\"tipo\": \"1\"}";
    private static final String MOCKITO_EXCEPTION_MESSAGE = "Excepción generado por Mockito";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;


    @MockBean
	private DomainCommandPort domainCommandPort;

    @MockBean
	private DomainQueryPort domainQueryPort;

	/** JUnit5. metodo que reemplaza a Before de JUnit 4. se ejecuta antes que cualquier test */
	@BeforeAll
	private static void setUp(){
        log.info("RUNNIG setUp");
	}

    @Test
    @Order(1)
    public void emptyResultGetMaestroTest()
    throws Exception {
        log.info("1 TEST emptyResultGetMaestroTest");
        // Setear url en base a propiedades del contexto/dominio
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(MAESTROS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String resultSS = result.getResponse().getContentAsString();
        log.info("emptyResultGetMaestroTest [RESULT SS] "+ resultSS);
        List<MaestroResponseBody> emptyList = objectMapper.readValue(resultSS, new TypeReference<List<MaestroResponseBody>>(){});
        log.info("emptyResultGetMaestroTest [RESULT] "+ emptyList);
        assertNotNull(resultSS, "chequeando no nulo al parsear el json de respuesta");
        assertNotNull(emptyList, "chequeando que la lista resultado del parseo no sea nula");
        assertTrue(() -> emptyList.size() == 0, "chequeando de la lista no contenga objetos");
    }

    @Test
    @Order(2)
    public void postMaestroTest()
    throws Exception {
        log.info("2 TEST putMaestroTest");

        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenReturn(
                CrearResourceResponse.builder()
                    .id(1L)
                    .fechaCreacion(LocalDateTime.now())
                .build()
            );
        // Setear url en base a propiedades del contexto/dominio
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content("{\"nombre\": \"maestro de prueba\",\"descripcion\": \"descripcion de prueba\",\"tipo\": \"1\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();
        
        String resultSS = result.getResponse().getContentAsString();
        log.info("postMaestroTest [RESULT SS] "+ resultSS);
        CrearResourceResponse crearResourceResponse = objectMapper.readValue(resultSS, CrearResourceResponse.class);
        log.info("postMaestroTest [RESULT] "+ crearResourceResponse);
        assertNotNull(resultSS, "Chequeando que la respuesta al insertar un maestro no sea nula");
        assertNotNull(crearResourceResponse, "chequeando que el response al insertar un maestro no sea nulo");
        assertTrue(() -> crearResourceResponse.getId() > 0, "Chequenado que el id autogenerado al insertar un maestro no sea nulo");
    }

    @Test
    @Order(3)
    public void postMaestroGatillaErrorDeValidacionTest()
    throws Exception {
        log.info("3 TEST postMaestroGatillaErrorDeValidacionTest");

        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenReturn(
                CrearResourceResponse.builder()
                    .id(1L)
                    .fechaCreacion(LocalDateTime.now())
                .build()
            );
        // Setear url en base a propiedades del contexto/dominio
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content("{\"nombre\": \"\",\"descripcion\": \"\",\"tipo\": \"1\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        
        String resultSS = result.getResponse().getContentAsString();
        log.info("postMaestroGatillaErrorDeValidacionTest [RESULT SS] "+ resultSS);
        ErrorBody errorBody = assertDoesNotThrow(() -> objectMapper.readValue(resultSS, ErrorBody.class), "Se espera un cuierpo de Error estándar");
        log.info("postMaestroGatillaErrorDeValidacionTest [RESULT] "+ errorBody);
        assertNotNull(errorBody, "Cuenrpo del mensaje de error no nulo");
        assertEquals(errorBody.getCode(), 400, "El códigom del mensaje de error debe ser Bad Request");
    }

    @Test
    @Order(4)
    public void getMaestroByIdAndDeleteTest()
    throws Exception {
        log.info("1 TEST emptyResultGetMaestroTest");

        Mockito.when(domainQueryPort.obtenerMaestro(anyLong())).thenReturn(
            MaestroModel.builder()
                .descripcion("descripcion de prueba obtenerMaestro")
                .id(1L)
                .nombre("nombre de prueba obtenerMaestro")
                .tipo(MaestroTipoEnum.TIPO5)
            .build()
        );

        // Setear url en base a propiedades del contexto/dominio
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(MAESTROS_ENDPOINT + 1L)
                //.content(birthday)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String resultSS = result.getResponse().getContentAsString();
        log.info("getMaestroByIdAndDeleteTest [RESULT SS] "+ resultSS);
        MaestroResponseBody response = objectMapper.readValue(resultSS, new TypeReference<MaestroResponseBody>(){});
        log.info("getMaestroByIdAndDeleteTest [RESULT] "+ response);
        assertNotNull(resultSS, "chequeando no nulo al parsear el json de respuesta");
        assertNotNull(response, "chequeando que la lista resultado del parseo no sea nula");
        assertTrue(() -> response.getId() == 1L, "chequeando que el identificador del resultado corresponda al posteado");


        // Setear url en base a propiedades del contexto/dominio
        mvc.perform(MockMvcRequestBuilders.delete(MAESTROS_ENDPOINT + 1L)
                //.content(birthday)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

    }


    /********************Testing de métodos de Controller Advisor*********************/

    @Test
    @Order(5)
    public void checkIllegalArgumentExceptionHadling() { 
        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(new IllegalArgumentException("Excepción generada por Mockito"));
        
        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

            log.info("checkIllegalArgumentExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta a argumento ilegal no puede ser una excepción");
    }

    @Test
    @Order(6)
    public void checkHttpMessageNotReadableExceptionHadling() {
        //Constructor con mensaje deprecado, por lo que crea un mock de la excepción
        HttpMessageNotReadableException exception = Mockito.mock(HttpMessageNotReadableException.class);
        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(exception);

        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
            
                log.info("checkHttpMessageNotReadableExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta a mensaje no legible no puede ser una excepción");
        
    }


    
    @Test
    @Order(7)
    public void checkServerWebInputExceptionHadling() {
        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(new ServerWebInputException(MOCKITO_EXCEPTION_MESSAGE));
        
        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

            log.info("checkServerWebInputExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta a mensaje Web Input no puede ser una excepción");
    }

    @Test
    @Order(8)
    public void checkResourceNotFoundExceptionHadling() {
        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(new ResourceNotFoundException(MOCKITO_EXCEPTION_MESSAGE));

        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

            log.info("checkResourceNotFoundExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta a solicitud de recurso inexistente no puede ser una excepción");
    }

    
    @Test
    @Order(9)
    public void checkResourceConflictExceptionHadling() {
        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(new ResourceConflictException(MOCKITO_EXCEPTION_MESSAGE));

        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();

            log.info("checkResourceConflictExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta a ingreso de recurso conflictivo no puede ser una excepción");
    }

    
    @Test
    @Order(10)
    public void checkExternalServiceExceptionHadling() 
    {
        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(new ExternalServiceException(MOCKITO_EXCEPTION_MESSAGE));

        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andReturn();

            log.info("checkExternalServiceExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta a llamada a servicio externo no puede ser una excepción");
    }

    @Test
    @Order(11)
    public void checkFeignExceptionHadling() {
        FeignException exception = Mockito.mock(FeignException.class);

        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(exception);
        
        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadGateway())
                .andReturn();

            log.info("checkFeignExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta a llamada a serivioc externo por proveedore feign no puede ser una excepción");
    }

    @Test
    @Order(12)
    public void checkRuntimeExceptionHadling() {

        Mockito.when(domainCommandPort.crearMaestro(any()))
            .thenThrow(new RuntimeException(MOCKITO_EXCEPTION_MESSAGE));
        
        assertDoesNotThrow( () -> {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post(MAESTROS_ENDPOINT)
                .content(ADVICE_MESSAGE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

            log.info("checkRuntimeExceptionHadling RESULT:" + result.getResponse().getContentAsString());
        }, "Respuesta error general no puede ser una excepción");
    }
}