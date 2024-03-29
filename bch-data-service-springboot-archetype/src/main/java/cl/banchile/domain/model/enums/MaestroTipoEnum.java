package cl.banchile.domain.model.enums;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración con los tipos de Maestro
 * se implementa un decoder
 * para que el parseador trasnforme numerico proveniente del JSON
 * en un tipo enumeración
 */
public enum MaestroTipoEnum {
    TIPO1(1),
    TIPO2(2),
    TIPO3(3),
    TIPO4(4),
    TIPO5(5);

    MaestroTipoEnum(int codigo){
        this.codigo = codigo;
    }

    private int codigo;

    @JsonValue
    public Integer getCodigo(){
        return this.codigo;
    }

    /**
     * Decodifica el parámetro Integer en una enumeración
     * @param codigo código a ser decodificado
     * @return Enumeración de tipo de maestro
     */
    @JsonCreator
    public static MaestroTipoEnum decode(final Integer codigo) {
		return Stream.of(MaestroTipoEnum.values()).filter(targetEnum -> targetEnum.codigo == codigo).findFirst().orElse(null);
	}

    /**
     * transforma en código int en una enumeración
     * @param codigo codigo de tipo maestro
     * @return Enumeración de tipo maestro, que corresponde al cóodigo
     */
    public static MaestroTipoEnum of(int codigo) {
        return Stream.of(MaestroTipoEnum.values())
          .filter(p -> p.getCodigo() == codigo)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}
