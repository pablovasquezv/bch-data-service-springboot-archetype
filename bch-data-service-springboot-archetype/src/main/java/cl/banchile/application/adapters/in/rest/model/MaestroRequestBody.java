package cl.banchile.application.adapters.in.rest.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import cl.banchile.domain.model.enums.MaestroTipoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo del mensaje para la creación de maestro.
 * aplica al rest controller y modelo el request body de la solicitud http.
 * Se incluyenm anotaciones de validación a sewr controladas
 * con el Bean Validation Framework
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaestroRequestBody {

    //Validación de input. no puede ser nulo ni blanco
    @NotBlank(message = "nombre es mandatorio")
    private String nombre;

    //Validación de input. no puede ser nulo ni blanco
    @NotBlank(message = "descripción es mandatoria")
    private String descripcion;

    /**
     * Para valores de tipo no string, no se puede usar notBlack
     * ya que valida la propiedadew parseada, no la recibida.
     */
    @NotNull(message = "tipo es mandatorio")
    private MaestroTipoEnum tipo;
}