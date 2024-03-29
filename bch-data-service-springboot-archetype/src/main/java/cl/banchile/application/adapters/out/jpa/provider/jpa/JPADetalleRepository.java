package cl.banchile.application.adapters.out.jpa.provider.jpa;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banchile.application.adapters.out.jpa.model.DetalleEntity;

/**
 * Interfaz declarativa de repositorio JPA de Detalle
 */
@Repository
public interface JPADetalleRepository extends JpaRepository<DetalleEntity, Long>{
    /**Se genera automáticamente el código para realizar consulta sobre entidad, con 'fec = :fecha' y mapeo hacia la entidad*/
    /**
     * Declarativa para encontrar un detalle por fecha
     * @param fecha fecha a buscar
     * @return optional con la entidad detalle
     */
    Optional<DetalleEntity> findByFecha(LocalDate fecha);
}
