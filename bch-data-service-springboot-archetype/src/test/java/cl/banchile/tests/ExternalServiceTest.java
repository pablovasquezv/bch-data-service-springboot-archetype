package cl.banchile.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import cl.banchile.application.adapters.out.externalservice.BoredApiAdapter;
import cl.banchile.application.adapters.out.externalservice.model.BoredActivityResponseBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class ExternalServiceTest {
    private BoredApiAdapter boredApiAdapter;
    private BoredActivityResponseBody mockedResponse;

    @BeforeAll
    public void setup(){
        mockedResponse = new BoredActivityResponseBody(
            "activity",
            "type",
            1L,
            100.0D,
            "http://url.cl",
            "key",
            10
        );

        boredApiAdapter = Mockito.mock(BoredApiAdapter.class);
        Mockito.when(boredApiAdapter.getBoredActivity())
            .thenReturn(
                mockedResponse
            );
    }

    @Test
    public void testExternalServiceCall(){
        log.info("[testExternalServiceCall]");
        BoredActivityResponseBody response = assertDoesNotThrow(boredApiAdapter::getBoredActivity, "Llamada a servicio externo no retorna error");
        assertNotNull(response, "Resultado no puede ser null");
        assertEquals(mockedResponse, response, "respuesta esperada y recibida son iguales");
        assertEquals(response.getAccessibility(), mockedResponse.getAccessibility(), "Accesibility es igual");
        assertEquals(response.getActivity(), mockedResponse.getActivity(), "Activity es igual");
        assertEquals(response.getKey(), mockedResponse.getKey(), "Key es igual");
        assertEquals(response.getParticipants(), mockedResponse.getParticipants(), "Participants es igual");
        assertEquals(response.getPrice(), mockedResponse.getPrice(), "Price es igual");
        assertEquals(response.getType(), mockedResponse.getType(), "Type es igual");
        assertEquals(response.getUrl(), mockedResponse.getUrl(), "url es igual");
    }
}
