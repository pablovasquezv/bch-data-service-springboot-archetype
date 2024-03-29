package cl.banchile.domain.ports.out;

import cl.banchile.domain.model.to.CrearResourceResponse;

/**
 * Puerto para publicación de eventos de integración
 */
public interface IntegrationEventPublisherPort {
    /**
     * publica eventode de integración al contexto
     * @param crearResourceResponse modelo genérico de creación de recurso de dominio
     */
    void publishMaestroCreadoEvent(CrearResourceResponse crearResourceResponse);
}