package cl.banchile.common.enums;

import java.util.stream.Stream;

/**
 * Enumeración con los eventos de integración soportados
 * deben incluirse tanto los tratados por este dominio asi como
 * los eventos de notificación de otros dominios del contexto
 */
public enum IntegrationEventTypeEnum {
    UNRECOGNIZED,
    CREAR_MAESTRO,
    CREAR_DETALLE,
    MAESTRO_CREADO,
    DETALLE_CREADO;

    @Override
    public String toString(){
        return this.name();
    }

    /**
     * transforma en código int en una enumeración
     * En caso de una operación no soportada, retorna el tipo UNTREATED
     * @param codigo codigo de tipo maestro
     * @return Enumeración de tipo maestro, que corresponde al cóodigo
     */
    public static IntegrationEventTypeEnum of(String codigo) {
        return Stream.of(IntegrationEventTypeEnum.values())
          .filter(p -> codigo.equals(p.toString()))
          .findFirst()
          .orElse(IntegrationEventTypeEnum.UNRECOGNIZED);
    }
}
