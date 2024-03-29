package cl.banchile.domain.model.enums;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Convertidor de enumeración de tipo MaestroTipoEnum a Integer. Usado implicitamente en el parser JSON global.
 * Aplica tanto al request body de adaptadores rest como a conversiones de entidades JPA a entidad de Base de datos.
 */
@Converter(autoApply = true)
public class MaestroTipoConverter implements AttributeConverter<MaestroTipoEnum, Integer> {
    
    /**
     * Implementa la conversión del tipo enumeración
     * a un integer
     */
    @Override
    public Integer convertToDatabaseColumn(MaestroTipoEnum canal) {
        if (canal == null) {
            return null;
        }
        return canal.getCodigo();
    }

    /**
     * implementa la conversión de un entero a 
     * un tipo enumeración
     */
    @Override
    public MaestroTipoEnum convertToEntityAttribute(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        return Stream.of(MaestroTipoEnum.values())
          .filter(c -> c.getCodigo() == codigo)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}