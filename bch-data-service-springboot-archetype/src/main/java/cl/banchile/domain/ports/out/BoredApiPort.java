package cl.banchile.domain.ports.out;

import cl.banchile.application.adapters.out.externalservice.model.BoredActivityResponseBody;

/**
 * Puerto de acceso a servicio externo Bored API
 * Puerto de salida
 */
public interface BoredApiPort {
    BoredActivityResponseBody getBoredActivity();
}
