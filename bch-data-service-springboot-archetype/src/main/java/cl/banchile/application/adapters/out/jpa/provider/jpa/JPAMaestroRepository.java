package cl.banchile.application.adapters.out.jpa.provider.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banchile.application.adapters.out.jpa.model.MaestroEntity;

/**
 * Interfaz declarativa de repositorio JPA de Maestro
 */
@Repository
public interface JPAMaestroRepository extends JpaRepository<MaestroEntity, Long>{
    /**
     * Declarativa para encontrar un maestro por nombre
     * @param nombre nombre del maestro a buscar
     * @return optional con la entidad maestro
     */
    Optional<MaestroEntity> findByNombre(String nombre);
}
