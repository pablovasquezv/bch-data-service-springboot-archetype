package cl.banchile.domain.ports.in;

import cl.banchile.domain.model.command.CrearDetalleCommandModel;
import cl.banchile.domain.model.command.CrearMaestroCommandModel;
import cl.banchile.domain.model.to.CrearResourceResponse;

/**
 * Puerto de acceso a los comandos de servicio de dominio
 */
public interface DomainCommandPort {

    /**
     * creación de recurso detalle, bajo un maestro
     * @param crearDetalleCommandModel modelo del comando CrearDetalle
     * @return modelo genérico de creación de recurso de dominio
     */
    CrearResourceResponse crearDetalle(CrearDetalleCommandModel crearDetalleCommandModel);

    /**
     * Eliminación de un detalle
     * @param idDetalle identificador del detalle
     */
    void eliminarDetalle(Long idDetalle );

    //Maestro Commands

    /**
     * Crear recurso maestro en el dominio
     * @param crearMaestroCommandModel Modelo del comando CrearMaestro
     * @return modelo genérico de creación de recurso de dominio
     */
    CrearResourceResponse crearMaestro(CrearMaestroCommandModel crearMaestroCommandModel);

    /**
     * Eliminar maestro
     * @param idMaestro identificador del maestro
     */
    void eliminarMaestro(Long idMaestro );
}