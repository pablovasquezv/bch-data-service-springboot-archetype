package cl.banchile.domain.ports.in;

import java.util.List;

import cl.banchile.domain.model.domain.MaestroModel;

/**
 * Puerto de acceso a consultas al servicio de dominio
 */
public interface DomainQueryPort {
    /**
     * Obtiene la lista de mestros registradoe en el dominio
     * @return lista con entidad de dominio maestros
     */
    List<MaestroModel> obtenerMaestros();

    /**
     * Obtiene un único Maestro, identificado por id
     * @param idMaestro identificador del recurso Maestro
     * @return modelo de dominio de Maestro
     */
    MaestroModel obtenerMaestro(Long idMaestro);
}