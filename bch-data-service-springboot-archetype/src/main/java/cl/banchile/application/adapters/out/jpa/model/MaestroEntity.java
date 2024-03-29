package cl.banchile.application.adapters.out.jpa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import cl.banchile.domain.model.enums.MaestroTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity JPA con estructura de la entidad de base de datos de Maestro
 * Anotaciones con secuancia autogenerada
 * especializada para Oracle
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y sin argumentos
 */
@Entity
@Table(name = "tglo_maestro")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaestroEntity {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name="MAESTRO_ID_GENERATOR", sequenceName="SA_FOL_FIC", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MAESTRO_ID_GENERATOR")
    private Long id;

    @Column(name = "nom")
    private String nombre;

    @Column(name = "des")
    private String descripcion;

    @Column(name = "tip")
    private MaestroTipoEnum tipo;  

}
