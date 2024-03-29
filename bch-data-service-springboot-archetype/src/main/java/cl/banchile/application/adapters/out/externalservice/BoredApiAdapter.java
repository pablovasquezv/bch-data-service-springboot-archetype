package cl.banchile.application.adapters.out.externalservice;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cl.banchile.application.adapters.out.externalservice.model.BoredActivityResponseBody;
import cl.banchile.domain.ports.out.BoredApiPort;

/**
 * Interfaz declarativa para llamada a servicios HTTP externos
 * Se requiere habilitar Spring Cloud Feign en la aplicación
 */
@RefreshScope
@FeignClient(value = "boder-api", url = "${cl.banchile.external-client.bored-api.url}")
public interface BoredApiAdapter extends BoredApiPort{
    
    // llama al método activity, a partir la propiedad base Url y mapea el resultado automáticamente
    @RequestMapping(method = RequestMethod.GET, value = "activity/", produces = "application/json")
    BoredActivityResponseBody getBoredActivity();
    
}
