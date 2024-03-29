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
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import cl.banchile.application.adapters.out.jdbc.JdbcDaoAdapter;
import cl.banchile.domain.model.domain.DetalleModel;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.model.enums.MaestroTipoEnum;

@JdbcTest
@TestInstance(Lifecycle.PER_CLASS)
@Sql(scripts={"classpath:schema.sql", "classpath:test-data.sql"})
@TestMethodOrder(OrderAnnotation.class)
public class JdbcAdapterTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcDaoAdapter jdbcDao;
    private Long  idMaestroTest = 0L;
    private Long  idDetalleTest = 0L;

    @BeforeAll
    public void init(){
        jdbcDao = new JdbcDaoAdapter(jdbcTemplate);
    }

    @Test
    @Order(1)
    public void testDetalleCount(){
        Integer count = jdbcDao.getAllDetalles().size();

        assertEquals(count, 0, "Chequeando 0 detalles en repo al inicio");
    }

    @Test
    @Order(2)
    public void testMaestroCount(){
        Integer count = jdbcDao.getAllMaestros().size();

        assertEquals(count, 2, "Chequeando 2 maestros al inicio, insertados por script de inicialización");
    }

    @Test
    @Order(3)
    public void testInsertQueryUpdateDeleteMaestro(){
        //Testear inserción del Maestro
        MaestroModel maestroModel = jdbcDao.insertMaestro(
            MaestroModel.builder()
                .descripcion("descripcion de prueba 1")
                .nombre("nombre de prueba")
                .tipo(MaestroTipoEnum.TIPO1)
                .build()
        );
        assertNotNull(maestroModel, "Verificando objeto de retorno no nulo");
        assertTrue(maestroModel.getId() > 0L, "verificando Id autogenerado de maestro mayor a cero");
        this.idMaestroTest = maestroModel.getId();

        //Actualizando el Maestro
        maestroModel.setDescripcion("descripcion modificada");
        MaestroModel updatedMaestro = jdbcDao.updateMaestro(maestroModel);
        assertNotNull(updatedMaestro,"Maestro actualizado no es nulo");
        assertEquals(maestroModel.getId(), updatedMaestro.getId(),"maestro por actualiza y actualizado con el mismo");

        //Obteniendo maentro por Id
        MaestroModel queriedMaestro = jdbcDao.getMaestroById(idMaestroTest).get();
        assertNotNull(queriedMaestro, "maestro consultado no es nulo");
        assertEquals(queriedMaestro.getId(), idMaestroTest, "Id maestro insertado es igual a mestro recuperado");
        assertEquals(queriedMaestro.getDescripcion(), "descripcion modificada", "descripcion de Maestro recuperado corresponde a la actualizada anteriormente");

        //Eliminando el maestro
        assertDoesNotThrow( () -> jdbcDao.deleteMaestro(queriedMaestro.getId()) , "La eliminación del maestro no retorna información");

        //Verificando eliminación. excepción específica para adaptador jdbc
        assertThrows(NoSuchElementException.class, () -> jdbcDao.getMaestroById(idMaestroTest).get(), "Elemento eliminado debe lanzar excepción al intentar recuperarlo");
        
        
    }

    @Test
    @Order(4)
    public void testInsertQueryUpdateDeleteDetalle(){
        DetalleModel detalleModel = jdbcDao.insertDetalle(
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
        DetalleModel detalle = jdbcDao.getDetalleById(idDetalleTest).get();
        assertNotNull(detalle, "Chequeando que el detalle insertado no sea nulo");
        assertTrue(detalle.getId() == idDetalleTest, "Chequeando que el id autogenerado del detalle sea mayor a cero");

        //Testeando retorno de servivio obtener todos los detalles
        List<DetalleModel> listaDetalle = jdbcDao.getAllDetalles();
        assertNotNull(listaDetalle, "Chequeando que lista detalles insertado no sea nula");
        assertTrue(listaDetalle.size() > 0, "Chequeando que la lista contenga valores");

        //testenado la actualización den un detalle previamente insertado
        detalle.setCantidad(detalle.getCantidad() + 100);
        DetalleModel detalleUpdated = jdbcDao.updateDetalle(detalle);
        assertNotNull(detalleUpdated, "Chequeando que el detalle no sea nulo");
        assertEquals(detalle.getCantidad(), detalleUpdated.getCantidad(), "Cantidad por actualizar debe ser igual a la actualizada");

    }

}
