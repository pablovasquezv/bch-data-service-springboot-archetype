package cl.banchile.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.banchile.common.exception.ResourceNotFoundException;
import cl.banchile.domain.model.command.CrearDetalleCommandModel;
import cl.banchile.domain.model.command.CrearMaestroCommandModel;
import cl.banchile.domain.model.domain.DetalleModel;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.model.to.CrearResourceResponse;
import cl.banchile.domain.ports.in.DomainCommandPort;
import cl.banchile.domain.ports.in.DomainQueryPort;
import cl.banchile.domain.ports.out.BoredApiPort;
import cl.banchile.domain.ports.out.DatabasePort;
import cl.banchile.domain.ports.out.IntegrationEventPublisherPort;
import lombok.extern.slf4j.Slf4j;

/**
 * implementación de Servicio de dominio
 * servicios con lógica de negocio
 * comunicación con el mundo externo es a través de puertos
 */
@Service
@Slf4j
public class DomainService implements DomainCommandPort, DomainQueryPort {

    private DatabasePort databasePort;
    private IntegrationEventPublisherPort integrationEventPublisherPort;
    private BoredApiPort boredApiPort;

    /**
     * Constructor con inyección de dependencia
     * @param databasePort puerto de base de datos
     * @param integrationEventPublisherPort puerto para puvlicación de evbentos de integración
     * @param boredApiPort puerto para acceso a servicio externo Bored API
     */
    public DomainService(
        @Qualifier("jpaDao") DatabasePort databasePort,
        @Autowired IntegrationEventPublisherPort integrationEventPublisherPort,
        @Autowired BoredApiPort boredApiPort
    ) {
        this.databasePort = databasePort;
        this.integrationEventPublisherPort = integrationEventPublisherPort;
        this.boredApiPort = boredApiPort;
    }

    /**
     * Obtener lista de maestros
     * persistidos en el dominio
     */
    @Override
    public List<MaestroModel> obtenerMaestros(){
        log.info("Service obtenerMaestros");

        log.info(boredApiPort.getBoredActivity().toString());

        return databasePort.getAllMaestros();
    }

    /**
     * Obtener un maestro persistido
     * identificado por el identificado único
     */
    @Override
    public MaestroModel obtenerMaestro(Long idMaestro){
        log.info("Service obtenerMaestro");

        return databasePort.getMaestroById(idMaestro)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Maestro con ID %s no encontrado", idMaestro)));

    }

    /**
     * Registra un maestro en el dominio para persistencia
     * retorna objeto genérico de creación de recurso
     */
    @Override
    @Transactional
    public CrearResourceResponse crearMaestro(CrearMaestroCommandModel crearMaestroCommand){
        log.info("Service crearMaestro");

        MaestroModel maestroModel = databasePort.insertMaestro(
            MaestroModel.builder()
                .nombre(crearMaestroCommand.getNombre())
                .descripcion(crearMaestroCommand.getDescripcion())
                .tipo(crearMaestroCommand.getTipo())
            .build()
        );

        CrearResourceResponse crearResourceResponse = CrearResourceResponse.builder()
                .id(maestroModel.getId())
                .fechaCreacion(LocalDateTime.now())
            .build();

        // Luego de una ejecución del comando, enviar evento de integración al contexto
        integrationEventPublisherPort.publishMaestroCreadoEvent(crearResourceResponse);
        
        return crearResourceResponse;
    }

    /**
     * Eliminar un maestro de la persistencia
     * también se eliminan sus detalles asociados
     */
    @Override
    @Transactional
    public void eliminarMaestro(Long idMaestro){
        log.info("Service eliminarMaestro");

        databasePort.deleteMaestro(idMaestro);
    }

    //Servicio de dominio de detalles


    /**
     * Crea un Detalle, asociado a un maestro por su identificador
     * retorna objeto genérico de creación de recurso en el dominio
     */
    @Override
    @Transactional
    public CrearResourceResponse crearDetalle(CrearDetalleCommandModel crearDetalleCommand){
        log.info("Service crearDetalle");

        DetalleModel detalleModel = databasePort.insertDetalle(
            DetalleModel.builder()
                .cantidad(crearDetalleCommand.getCantidad())
                .fecha(crearDetalleCommand.getFecha())
                .idMaestro(crearDetalleCommand.getIdMaestro())
                .monto(crearDetalleCommand.getMonto())
                .build()
        );
        
        return
            CrearResourceResponse.builder()
                .id(detalleModel.getId())
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    /**
     * Eliminar un detalle desde la persistencia
     */
    @Override
    @Transactional
    public void eliminarDetalle(Long idDetalle){
        log.info("Service eliminarDetalle");
        databasePort.deleteDetalle(idDetalle);
        return;
    }

}
