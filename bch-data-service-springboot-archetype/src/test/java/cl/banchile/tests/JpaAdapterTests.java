package cl.banchile.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import cl.banchile.application.adapters.out.jpa.JpaDaoAdapter;
import cl.banchile.application.adapters.out.jpa.provider.jpa.JPADetalleRepository;
import cl.banchile.application.adapters.out.jpa.provider.jpa.JPAMaestroRepository;
import cl.banchile.domain.model.domain.DetalleModel;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.model.enums.MaestroTipoEnum;
import lombok.extern.slf4j.Slf4j;


/**
 * Test de JPA Adapter
 * Adaptador que usa Spring Data JPA Repositories para la persistencia
 * se iguala en funciones y características a JDBCTemplate
 */
@Slf4j
@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class JpaAdapterTests {

	@Autowired
	private JPAMaestroRepository maestroRepository;
	
	@Autowired
	private JPADetalleRepository detalleRepository;

	private JpaDaoAdapter jpaDao;

    @BeforeAll
    public void init(){
        jpaDao = new JpaDaoAdapter(detalleRepository, maestroRepository);
    }

    /**
     * Test de integración
     * para CRUD de maestro
     */
    @Test
    @Order(1)
    public void testInsertQueryUpdateDeleteMaestro(){
        Long idMaestroTest;
        //Testear inserción del Maestro
        MaestroModel maestroModel = jpaDao.insertMaestro(
            MaestroModel.builder()
                .descripcion("descripcion de prueba 1")
                .nombre("nombre de prueba")
                .tipo(MaestroTipoEnum.TIPO1)
                .build()
        );
        assertNotNull(maestroModel, "Verificando objeto de retorno no nulo");
        assertTrue(maestroModel.getId() > 0L, "verificando Id autogenerado de maestro mayor a cero");
        idMaestroTest = maestroModel.getId();
        log.info("*****ID MESTRO PARA TEST: {}", idMaestroTest);

        //Actualizando el Maestro
        maestroModel.setDescripcion("descripcion modificada");
        MaestroModel updatedMaestro = jpaDao.updateMaestro(maestroModel);
        assertNotNull(updatedMaestro,"Maestro actualizado no es nulo");
        assertEquals(maestroModel.getId(), updatedMaestro.getId(),"maestro por actualiza y actualizado con el mismo");

        //Obteniendo maentro por Id
        MaestroModel queriedMaestro = jpaDao.getMaestroById(idMaestroTest).get();
        assertNotNull(queriedMaestro, "maestro consultado no es nulo");
        assertEquals(queriedMaestro.getId(), idMaestroTest, "Id maestro insertado es igual a mestro recuperado");
        assertEquals(queriedMaestro.getDescripcion(), "descripcion modificada", "descripcion de Maestro recuperado corresponde a la actualizada anteriormente");

        //Eliminando el maestro
        assertDoesNotThrow( () -> jpaDao.deleteMaestro(queriedMaestro.getId()) , "La eliminación del maestro no retorna información");

        //Verificando eliminación. excepción específica para adaptador jdbc
        assertThrows(NoSuchElementException.class, () -> jpaDao.getMaestroById(idMaestroTest).get(), "Elemento eliminado debe lanzar excepción al intentar recuperarlo");
        
        
    }

    /**
     * Test de integración
     * para CRUD de maestro
     */
    @Test
    @Order(2)
    public void testInsertQueryUpdateDeleteDetalle(){
        Long idMaestroTest;
        Long idDetalleTest;
        //insertando un maestro, pere generar ID y entidad
        MaestroModel maestroModel = jpaDao.insertMaestro(
            MaestroModel.builder()
                .descripcion("descripcion de prueba 1")
                .nombre("nombre de prueba")
                .tipo(MaestroTipoEnum.TIPO1)
                .build()
        );
        idMaestroTest = maestroModel.getId();
        
        // Insertando un detalle, referenciando a maestro por identificador
        DetalleModel detalleModel = jpaDao.insertDetalle(
            DetalleModel.builder()
                .cantidad(10)
                .fecha(LocalDate.now())
                .idMaestro(idMaestroTest)
                .monto(105.3)
                .build()
        );

        assertNotNull(detalleModel, "Chequenado que el detalle insertado no sea nulo");
        assertTrue(detalleModel.getId() > 0L, "Chequeando que el id autogenerado del detalle sea mayor a cero");

        idDetalleTest = detalleModel.getId();

        // Testeando le recuperación del detalle recién insertado
        DetalleModel detalle = jpaDao.getDetalleById(idDetalleTest).get();
        assertNotNull(detalle, "Chequeando que el detalle insertado no sea nulo");
        assertTrue(detalle.getId() == idDetalleTest, "Chequeando que el id autogenerado del detalle sea mayor a cero");

        //Testeando retorno de servivio obtener todos los detalles
        List<DetalleModel> listaDetalle = jpaDao.getAllDetalles();
        assertNotNull(listaDetalle, "Chequeando que lista detalles insertado no sea nula");
        assertTrue(listaDetalle.size() > 0, "Chequeando que la lista contenga valores");

        //testenado la actualización den un detalle previamente insertado
        detalle.setCantidad(detalle.getCantidad() + 100);
        DetalleModel detalleUpdated = jpaDao.updateDetalle(detalle);
        assertNotNull(detalleUpdated, "Chequeando que el detalle no sea nulo");
        assertEquals(detalle.getCantidad(), detalleUpdated.getCantidad(), "Cantidad por actualizar debe ser igual a la actualizada");

    }

}
