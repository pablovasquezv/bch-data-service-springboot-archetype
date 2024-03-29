[![Quality Gate Status](http://codequality.banchile.cl/api/project_badges/measure?project=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype&metric=alert_status)](http://codequality.banchile.cl/dashboard?id=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype)
[![Reliability Rating](http://codequality.banchile.cl/api/project_badges/measure?project=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype&metric=reliability_rating)](http://codequality.banchile.cl/dashboard?id=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype)
[![Security Rating](http://codequality.banchile.cl/api/project_badges/measure?project=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype&metric=security_rating)](http://codequality.banchile.cl/dashboard?id=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype)
[![Technical Debt](http://codequality.banchile.cl/api/project_badges/measure?project=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype&metric=sqale_index)](http://codequality.banchile.cl/dashboard?id=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype)
[![Vulnerabilities](http://codequality.banchile.cl/api/project_badges/measure?project=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype&metric=vulnerabilities)](http://codequality.banchile.cl/dashboard?id=arquitectura-software_arquetipos_java_bch-data-service-springboot-archetype)

## Changelog 1.0.0 - Release 20211109
- Release inicial del Arquetipo

# Banchile Springboot Archetype
Arquetipo para el desarrollo de microservicios en Spring Boot, con arquitectura Hexagonal

## Que es un Microservicio?
También conocido como Arquitectura de Microservicios, es un estilo de arquitectura que **estructura una aplicación como una colección de servicios** que son:
- Altamente mantenibles y testeables.
- Con bajo Acoplamiento.
- Deployables rápida e independientemente.
- Organizados en torno a capacidades de negocio.
- pertenecientes o desarrollados por un equipo determinado.

Una arquitectura de microservicios habilita una rápida, frecuente y confiable entrega de **grandes y complejas aplicaciones**. también ayuda a una compañia a organizar y evolucionar su stack tecnológico.

## Los microservicios son una bala de plata?
Pese a los beneficios, la arquitectura de microservicios **tiene varios inconvenientes** que deben ser resueltos.
Para una aplicación de alcance limitado, bajos requerimientos técnicos y funcionales, **la arquitectura monilítica** será siempre una opción.

## Patrones, Patrones, Patrones
Para diseñar y entender una arquitecturta de microservicios, se debe tener una comprensión de los **patrones de microservicio**, que no son mas que una colección de patrones aplicables a la arquitectura.
Algunos patrones de microservicios son:
- Api Gateway: definie cómo los clientes van a acceder a un servicio en la arquitectura de microservicios
- Database per Service: describe cómo un servicio tiene su propia base de datos para garantizar el bajo acoplamiento
- Service Discovery: usados para "descubrir" instancias de ejecución y habilitar un punto de enrutamiento
- Circuit breaker: describe el comportamiento ante posibles fallas en la comunicación síncrona entre microservicios, con el fin de evitar que se desencadene un fallo masivo
- CQRS: o responsabilidad segregada de comandos y consultas. Cuando existe una clara diferencia entre la carga de "escritura" y "lectura" de un microservicio, se puede aplicar este patrón para separar las responsabilidades de lectura y escritura, que puede ser a nivel de controlador, servie, Repository o incluso segragando la base de datos de escritura y de lectura.

Para comprender mas sobre patrones de microservicios, se recomenda el siguiente contenido introductorio: [Microservices Patterns](https://microservices.io/patterns/index.html)

## Que es Arquitectura Hexagonal?
La arquitectura hexagonal o "patrón de Puertos y Adaptadores" es un modelo para diseñar aplicaciones, propuesto por Alistair Cockburn en 2005, donde la idea principal es poner los **Inputs y Outputs en los bordes del diseño** con el fin de aislar la lógica de negocio (nucleo) de preocupaciones externas, permitiendo que estos Inputs y Outputs pueden ser extirpados, agregados o modificados **sin impacto en la lógica del negocio**
Esto permite que las aplicaciones con bajo acoplamiento y sean mas fáciles de modificar y testear.

![Arquitectura Hexagonal](/src/main/resources/static/hexa.png)

### Adaptadores (de tecnologías)
Los adaptadores son las implementaciones concretas de las interfaces de entrada y salida al mundo exterior.
Existen 2 tipos de adaptadores:
- Driving (Input): ... una tecnología específica y se comunican con el núcleo a través de una interfaz bien conocida o **puertos de entrada**
- Driven (Output): ... una tecnología específica e implementan un **puerto de salida**

### Puertos (o interfaces)
Son contratos madiante las cuales se comunican los componentes de software. En concreto interfaces las que los Adaptadores de entrada utilizan para ejecutar lógica del core del negocio o el core del negocio utiliza para acceder a recursos externos.

### Servicios de Application y Domain
Son servicios que ofrecen funcionalidades tanto en capa de Aplicación como en capa de Dominio (Negocio).
Los servicios de Dominio exponen la lógica de negocio a travez de un puerto de entrada, las que puede ser consumida por los servicios de la capa de Aplicación o directamente desde los adaptadores.
Los servicios de Aplicación permiten una capa adicional entre los Adaptadores de entrada y el dominio, permitiendo composición e includo integración directa con adaptadores de salida.

### Dependencias
En Arquitectura Hexagonal existe una máxima: **"El core del dominio no conoce el mundo exterior"**. Esto implica que por ejemplo, un adaptador de entrada Rest recibe una estructura de datos que representa un RequestBody, no puede (debe) usar esa misma estructura como parámetro de un servicio de Dominio, ya que el dominio no conoce de espeficidades de cada tecnología.
Para llamar a un servicio de dominio con parámetros, estos deben ser compatibles con la lógica del dominio y no con la tecnología, lo que se traduce en una activo parseo de estructuras entre las distintas capas.
Cuando el dominio retorna ante un requerimiento de un adaptador o de un servicio de aplicación, es quien hizo el requerimiento el responsable de traducir esa entidad de dominio en una estructura válida para la tecnología que implementa.


## Implementación del Arquetipo Spring Boot con Arquitectura Hexagonal

### Estructura del Arquetipo 
![Estructura Raiz](/src/main/resources/static/raiz.png)

Mas allá de la estructura común de los proyectos Java, se crean 2 packages en la raiz del source (src/main/java/cl/banchile) que representan las capas de la arquitectura hexagonal: Aplication y Domain. Además de una tercera para componentes comunes.

#### Application
![Estructura Application](/src/main/resources/static/application.png)

El package **application** aloja las implementaciones de cada adapter, tanto de entrada como se salida, así como servicios de aplicación. ** para la implementación actual del arquetipo, se omite el uso de servicios de aplicación**

#### Domain
![Estructura Domain](/src/main/resources/static/domain.png)

el package **domain** aloja la lógica y entidades de negocio, así como también los **puertos**, tanto de entrada como de salida.

#### Common
Aloja configuraciones específicas de Spring Boot, Excepciones globales y cualquier artefacto que sea transversal a la implementación.


### Nomenclatura de Componentes

Se recomienda Utilizar una nomenclatura clara y concisa, explicitando el objetivo del artefacto.

- [Application] Adapters: **\<Funcionalidad/Tecnología\>Adapter.java** Cada adapter debe ser alojado en un package independiente, que explicite la tecnología implementada
- [Application] Adapter Models: Libre, dependiendo de la tecnología. Ej. MaestroRequestBody.java
- [Application] Application Services: **ApplicationService.java** para el caso de la implementación actual, se omite el Servicio de Aplicación.
- [Domain] Domain Model: **\<Entidad\>Model.java** representan las entidades del core de negocio, con propiedades expresadas en el lenguaje del negocio.
- [Domain] Domain Command: **\<Comando\>CommandModel.java** representa eventos que producen un cambio en el estado del dominio con nomenclatura en clave "frase verbal", por ejemplo: AgregarMaestroCommandModel.java
- [Domain] Domain Query: **\<Consulta\>QueryModel.java** representa preguntas sobre el modelo que no producen cambios en el estado del dominio, en clave "frase verbal". Por ejemplo: BuscarMaestroQueryModel.java
- [Domain] Input Port: **DomainCommandPort.java, DomainQueryPort.java** Generalmente los puertos de entrada son implementados por el servicio de dominio. Se usa un acercamiento a CQRS, en que se separan los puertos de comando y consulta.
- [Domain] Output Port: **\<Funcionalidad/Infraestructura\>Port.java** Los puertos de salida son interfaces que representan funcionalidades externalizadas, donde NO se debe explicitar la tecnología (como los adapters), pero SI la funcionalidad ligada a la infraestructura. Ej: DatabasePort.java, IntegrationEventPublisherPort.java
- [Domain] Domain Service: **DomainService.java**


### Framework(Less)

Como base de la implementación, se utiliza el Framework Light, implementado como un Parent tipo starter donde se manejan dependecias y definiciones para que cualquier desarrollo que extienda de el, pueda ser considerado en los pipelines de liberación.
Para mayor detalle, ver [Link Conluence](https://confluence.bantcent.cl/pages/viewpage.action?pageId=48627752) [Link GitLab](http://git.bantcent.cl/nofwkbch/springboot/bch-starter-springboot-java8).

### Configuración de Ejecución

Para ejecutar un servicio basado en este arquetipo, se debe seguir la misma nomenclaruta de ejecución que los microservicios actuales en plataformas BALIRMQ y BALISBOOT, con la salvedad que se debe explicitar **La clase Main**, en este caso cl.banchile.BIApplication


A continuación, un ejemplo de configuración de ejecución para **VS Code**
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch BIApplication",
            "request": "launch",
            "mainClass": "cl.banchile.BIApplication",
            "projectName": "bch-data-service-springboot-archetype",
            "vmArgs": "-Dlogging.config=C:\\JAVADEV\\services-config-desarrollo\\logback.xml -Dproject.artifactId=bch-data-service-springboot-archetype -Dproject.version=1.0.0-SNAPSHOT -Dspring.config.location=C:\\JAVADEV\\services-config-desarrollo\\application-desarrollo.yml,C:\\JAVADEV\\services-config-desarrollo\\application.yml,C:\\JAVADEV\\services-config-desarrollo\\bch-data-service-springboot-archetype\\application.yml -Xms50m -Xmx200m -XX:MaxMetaspaceSize=100m -XX:CompressedClassSpaceSize=100m -XX:+UseG1GC -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30"
        }
    ]
}
```
