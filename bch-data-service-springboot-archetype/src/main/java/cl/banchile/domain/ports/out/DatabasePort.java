package cl.banchile.domain.ports.out;

import java.util.List;
import java.util.Optional;

import cl.banchile.domain.model.domain.DetalleModel;
import cl.banchile.domain.model.domain.MaestroModel;

/**
 * Puerto de acceso a servicios de base de datos
 */
public interface DatabasePort {

    /**
     * Obtiene un detalle por id
     * @param idDetalle id del detalle
     * @return modelo del detalle
     */
    Optional<DetalleModel> getDetalleById(Long idDetalle);

    /**
     * obtiene todos los detalle registrados en el dominio
     * @return lista con los detalles
     */
    List<DetalleModel> getAllDetalles();
    
    /**
     * inserta un detalle en base de datos
     * @param detalleModel modelo de dominio del detalle a insertar
     * @return Modelo del dominio con el detalle insertado. En condiciones nomales es la misma entidad mas id autogenerado.
     */
    DetalleModel insertDetalle(DetalleModel detalleModel);

    /**
     * actualiza un detalle
     * @param detalleModel modelo de dominio del detalle
     * @return modelo de domino con el detalle actualizado.
     */
    DetalleModel updateDetalle(DetalleModel detalleModel);

    /**
     * Elimina un detalle
     * @param id identificador de detalle
     */
    void deleteDetalle(Long id);



    //Maestro

    /**
     * Obtiene un maestro por id
     * @param idMaestro identificador del maestro
     * @return Optional con el maestro
     */
    Optional<MaestroModel> getMaestroById(Long idMaestro);

    /**
     * Obtiene todos los maestros registrados en el dominio
     * @return lista con los maestros
     */
    List<MaestroModel> getAllMaestros();
    
    /**
     * inserta un maestro en la base de datos
     * @param maestroModel modelo de dominio del maestro a insertar
     * @return modelo de dominio del maestro insertado. En condiciones normales, incluye el id autogenerado
     */
    MaestroModel insertMaestro(MaestroModel maestroModel);

    /**
     * Elimiar una maestro
     * @param id identificador del maestro a eliminar
     */
    void deleteMaestro(Long id);

    /**
     * Actualiza una maestro
     * @param maestroModel Modelo de dominio del maestro a actualizar
     * @return modelo de dominio con el maestro actualizado
     */
    MaestroModel updateMaestro(MaestroModel maestroModel);
}
