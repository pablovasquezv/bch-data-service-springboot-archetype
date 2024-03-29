package cl.banchile.application.adapters.out.jpa.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity JPA con estructura de la entidad de base de datos de Detalle
 * Anotaciones con secuancia autogenerada
 * especializada para Oracle
 * Lombok para la omisión de código redundante
 * Implementa patrón builder
 * Getters, setters, equals y hashcode con @Data
 * Constructor con y sin argumentos
 */
@Entity
@Table(name = "tglo_detalle")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleEntity {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name="DETALLE_ID_GENERATOR", sequenceName="SA_FOL_FIC", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DETALLE_ID_GENERATOR")
    private Long id;

    @Column(name = "fec")
    private LocalDate fecha;

    @Column(name = "cnt")
    private Integer cantidad;

    @Column(name = "mnt")
    private Double monto;

    @OneToOne
    @JoinColumn(name = "id_mae")
    private MaestroEntity maestro;

}
