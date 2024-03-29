package cl.banchile.application.adapters.out.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cl.banchile.application.adapters.out.jpa.model.DetalleEntity;
import cl.banchile.application.adapters.out.jpa.model.MaestroEntity;
import cl.banchile.application.adapters.out.jpa.provider.jpa.JPADetalleRepository;
import cl.banchile.application.adapters.out.jpa.provider.jpa.JPAMaestroRepository;
import cl.banchile.common.exception.ResourceNotFoundException;
import cl.banchile.domain.model.domain.DetalleModel;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.ports.out.DatabasePort;

/**
 * Adaptador que implementa el puerto de acceso a Base de datos
 * Qualifier espcífico ya que en arquetipo base hay 2 benas que implementanm la interface
 * DatabasePort
 */
@Component
@Qualifier("jpaDao")
public class JpaDaoAdapter implements DatabasePort {
    private JPADetalleRepository detalleRepository;
    private JPAMaestroRepository maestroRepository;

    /**
     * Constructor con inyección de dependencias
     * @param detalleRepository JPA Repository del detalle entity
     * @param maestroRepository JPA Repository del maestro entity
     */
    public JpaDaoAdapter(
        @Autowired JPADetalleRepository detalleRepository,
        @Autowired JPAMaestroRepository maestroRepository
    ){
        this.detalleRepository = detalleRepository;
        this.maestroRepository = maestroRepository;
    }
    
    /**
     * Método para obtener detalle por identificador
     * utiliza JPA Rerpository
     * Para evidenciar que resultado puede no existir, retorna Optional
     */
    @Override
    public Optional<DetalleModel> getDetalleById(Long idDetalle){
        return detalleRepository.findById(idDetalle)
            .map(JpaDaoAdapter::detalleEntity2DetalleModel);
            
    }

    /**
     * Método para obtener todos los detalles registrados
     * utiliza JPA Repository
     */
    @Override
    public List<DetalleModel> getAllDetalles(){
        return detalleRepository.findAll()
            .stream()
            .map(JpaDaoAdapter::detalleEntity2DetalleModel)
            .collect(Collectors.toList());

    }
    
    /**
     * Método para insertar un detalle
     * utiliza JPA Repository
     */
    @Override
    public DetalleModel insertDetalle(DetalleModel detalleModel){
        MaestroEntity maestroEntity = null;
        if(detalleModel.getIdMaestro() != null){
            maestroEntity = maestroRepository.findById(detalleModel.getIdMaestro())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("No se ha encontrado el registro maestro referenciado con ID %s",detalleModel.getIdMaestro() )) );
        }

        DetalleEntity detalleEntity =  detalleRepository.save(
            DetalleEntity.builder()
                .cantidad(detalleModel.getCantidad())
                .fecha(detalleModel.getFecha())
                .maestro(maestroEntity)
                .monto(detalleModel.getMonto())
                .build()
        );

        return DetalleModel.builder()
            .id(detalleEntity.getId())
            .idMaestro(detalleEntity.getMaestro().getId())
            .cantidad(detalleEntity.getCantidad())
            .fecha(detalleEntity.getFecha())
            .monto(detalleEntity.getMonto())
        .build();
    }

    /**
     * Método para actualizar un detalle
     * utiliza JPA Repository
     */
    @Override
    public DetalleModel updateDetalle(DetalleModel detalleModel){

        MaestroEntity maestroEntity = maestroRepository.findById(detalleModel.getIdMaestro())
            .orElseThrow(() -> new ResourceNotFoundException(String.format("No se ha encontrado el registro maestro referenciado con ID %s",detalleModel.getIdMaestro() )) );

        DetalleEntity detalleEntity = 
            detalleRepository.findById(detalleModel.getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("No se ha encontrado el registro detalle a actualizar con ID %s", detalleModel.getId() )) );
        
        //Modificando y actualizando la entidad
        detalleEntity.setCantidad(detalleModel.getCantidad());
        detalleEntity.setFecha(detalleModel.getFecha());
        detalleEntity.setMaestro(maestroEntity);
        detalleEntity.setMonto(detalleModel.getMonto());
        DetalleEntity updatedEntity = detalleRepository.save(detalleEntity);

        return DetalleModel.builder()
            .id(updatedEntity.getId())
            .idMaestro(updatedEntity.getMaestro().getId())
            .cantidad(updatedEntity.getCantidad())
            .fecha(updatedEntity.getFecha())
            .monto(updatedEntity.getMonto())
        .build();
    }

    /**
     * Método para EliminarDetalle
     * utiliza JPA Repository
     */
    @Override
    public void deleteDetalle(Long id){
        detalleRepository.findById(id)
            .ifPresent( toDelete -> detalleRepository.delete(toDelete));
    }

    // Métodos de persistencia de maestro
    
    /**
     * Método para obtener un maestro por identificador
     * utiliza JPA Resppository
     * RetornaOptional para evidenciar que puede no existir
     */
    @Override
    public Optional<MaestroModel> getMaestroById(Long idMaestro){
        return maestroRepository.findById(idMaestro)
            .map(JpaDaoAdapter::maestroEntity2MaestroModel);
    }

    /**
     * Método para obtener todos los maestros registrados
     * utiliza JPA Repository
     */
    @Override
    public List<MaestroModel> getAllMaestros(){
        return maestroRepository.findAll()
            .stream()
            .map(JpaDaoAdapter::maestroEntity2MaestroModel)
            .collect(Collectors.toList());
    }
    
    /**
     * Método para insertar un Maestro
     * utiliza JPA Respository
     */
    @Override
    public MaestroModel insertMaestro(MaestroModel maestroModel){

        MaestroEntity maestroEntity = maestroRepository.save(
            MaestroEntity.builder()
                .descripcion(maestroModel.getDescripcion())
                .nombre(maestroModel.getNombre())
                .tipo(maestroModel.getTipo())
                .build()
        );

        return MaestroModel.builder()
            .descripcion(maestroEntity.getDescripcion())
            .id(maestroEntity.getId())
            .nombre(maestroEntity.getNombre())
            .tipo(maestroEntity.getTipo())
        .build();
    }

    /**
     * Método para actualizar un maestro
     * utiliza JdbcTemplate
     */
    @Override
    public MaestroModel updateMaestro(MaestroModel maestroModel){
        MaestroEntity maestroEntity = 
            maestroRepository.findById(maestroModel.getId())
                .map( entity -> {
                    
                    return entity;
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("No se ha encontrado el registro maestro a actualizar con ID %s",maestroModel.getId() )) );

        maestroEntity.setDescripcion(maestroModel.getDescripcion());
        maestroEntity.setNombre(maestroModel.getNombre());
        maestroEntity.setTipo(maestroModel.getTipo());
        MaestroEntity updatedEntity = maestroRepository.save(maestroEntity);

        return MaestroModel.builder()
            .descripcion(updatedEntity.getDescripcion())
            .id(updatedEntity.getId())
            .nombre(updatedEntity.getNombre())
            .tipo(updatedEntity.getTipo())
        .build();
    }

    /**
     * Método para eliminar un maestro
     * utiliza JPA Respository
     */
    @Override
    public void deleteMaestro(Long idMaestro){
        maestroRepository.findById(idMaestro)
        .ifPresent( toDelete -> maestroRepository.delete(toDelete));
    }

    /**
     * Permite la trasnformación de una entidad JPA a un modelo de dominio
     * @param entity entidad JPA a trasnformar
     * @return Modelo de dominio
     */
    private static DetalleModel detalleEntity2DetalleModel(DetalleEntity entity){
        return DetalleModel.builder()
        .cantidad(entity.getCantidad())
        .fecha(entity.getFecha())
        .id(entity.getId())
        .idMaestro(Optional.ofNullable(entity.getMaestro()).map(MaestroEntity::getId).orElse(null))
        .monto(entity.getMonto())
        .build();
    }

    /**
     * Permite la trasnformación de una entidad JPA a un modelo de dominio
     * @param entity entidad JPA a trasnformar
     * @return Modelo de dominio
     */
    private static MaestroModel maestroEntity2MaestroModel(MaestroEntity entity){
        return MaestroModel.builder()
            .descripcion(entity.getDescripcion())
            .id(entity.getId())
            .nombre(entity.getNombre())
            .tipo(entity.getTipo())
        .build();
    }
}

