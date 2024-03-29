package cl.banchile.application.adapters.in.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banchile.application.adapters.in.rest.model.MaestroRequestBody;
import cl.banchile.application.adapters.in.rest.model.MaestroResponseBody;
import cl.banchile.domain.model.command.CrearMaestroCommandModel;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.model.to.CrearResourceResponse;
import cl.banchile.domain.ports.in.DomainCommandPort;
import cl.banchile.domain.ports.in.DomainQueryPort;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase de implementación de Adaptador Rest
 * Rest Controller
 */
@Slf4j
@RestController
// Raiz de URI Rest controller concide con lineamientos de arquitectura
// se considera en nombre del contexto, la versión y el dominio del servicio
@RequestMapping(path = "/${archetype.context-name}/${archetype.current-version}/${archetype.domain-name}")
@OpenAPIDefinition(
    info = @Info(
        title = "Banchile Dominio Maestro Rest API",
        version = "1"
    )
)
public class RestControllerAdapter {
    private DomainCommandPort domainCommandDrivingPort;
    private DomainQueryPort domainQueryDrivingPort;

    /**
     * Constructor con inyección de dependencias
     * @param domainCommandDrivingPort puerto de comandos al dominio
     * @param domainQueryDrivingPort puerto de consultas al dominio
     */
    public RestControllerAdapter(
        @Autowired DomainCommandPort domainCommandDrivingPort,
        @Autowired DomainQueryPort domainQueryDrivingPort
    ) {
        this.domainCommandDrivingPort = domainCommandDrivingPort;
        this.domainQueryDrivingPort = domainQueryDrivingPort;
    }

    /**
     * obtener una lista de los maestros registrados
     * @return lista con los maestros registrados
     */
    @GetMapping("")
    @Operation(summary = "listar todos los maestros")
    public ResponseEntity<List<MaestroResponseBody>> obtenerMaestros(){
        log.info("Rest Adapter obtenerMaestros");

        //Llamando al servicio de dominio obtenerMaestros
        List<MaestroResponseBody> lista = domainQueryDrivingPort.obtenerMaestros()
            .stream()
            .map( maestroModel -> 
                MaestroResponseBody.builder()
                    .id(maestroModel.getId())
                    .descripcion(maestroModel.getDescripcion())
                    .nombre(maestroModel.getNombre())
                    .tipo(maestroModel.getTipo())
                .build()
            )
            .collect(Collectors.toList());
        
        return new ResponseEntity<>(lista,HttpStatus.OK);
    }

    /**
     * Obtiene un maestro, identificado por su id
     * @param idMaestro identificador del maestro
     * @return Response con el maestro
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un maestro")
    public ResponseEntity<MaestroResponseBody> obtenerMaestro(@PathVariable(name = "id", required = false) Long idMaestro){
        log.info("Rest Adapter obtenerMaestro");

        //Llamando el servicio de dominio obtenerMaestro, por identificador
        MaestroModel maestroModel = domainQueryDrivingPort.obtenerMaestro(idMaestro);
        
        return new ResponseEntity<>(
            MaestroResponseBody.builder()
                .descripcion(maestroModel.getDescripcion())
                .id(maestroModel.getId())
                .nombre(maestroModel.getNombre())
                .tipo(maestroModel.getTipo())
            .build()
            ,HttpStatus.OK
        );
    }

    /**
     * Crear un Maestro
     * @param crearMaestroRequest cuerpo de la solicitud
     * @return response con los detalles de la creación del recurso
     */
    @PostMapping("")
    @Operation(summary = "registrar un maestro")
    public ResponseEntity<CrearResourceResponse> crearMaestro(@Valid @RequestBody MaestroRequestBody crearMaestroRequest){
        log.info("Rest Adapter crearMaestro");

        CrearMaestroCommandModel command = CrearMaestroCommandModel.builder()
            .descripcion(crearMaestroRequest.getDescripcion())
            .nombre(crearMaestroRequest.getNombre())
            .tipo(crearMaestroRequest.getTipo())
        .build();
            
        //llamando al servicio de dominio crearMaestro
        return new ResponseEntity<>(domainCommandDrivingPort.crearMaestro(command),HttpStatus.ACCEPTED);
    }

    /**
     * Eliminar un maestro, junto a todos sus detalles
     * @param idMaestro id del maestro a eliminar
     * @return responseEntity con status OK
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un maestro, junto a su detalle")
    public ResponseEntity<Object> eliminarMaestro(@PathVariable(name = "id", required = true) Long idMaestro){
        log.info("Rest Adapter eliminarMaestro");
        //llamando al servicio de dominio eliminarMaestro
        domainCommandDrivingPort.eliminarMaestro(idMaestro);
        return ResponseEntity.ok().build();
    }



}