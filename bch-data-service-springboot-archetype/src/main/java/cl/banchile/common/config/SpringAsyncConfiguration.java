package cl.banchile.common.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase de configuración del asyn Executor
 * asociado a la anotación Async
 */
@Configuration
@EnableAsync
@Slf4j
public class SpringAsyncConfiguration implements AsyncConfigurer {

	private Integer corePoolSize;
	private Integer maxPoolSize;
	private Integer queueCapacity;

	/**
	 * Constructor con inyección de propiedades
	 * @param corePoolSize tamaño inicial del poor de hilos de ejecución asyncrona
	 * @param maxPoolSize tamaño máximo del poor de hilos de ejecución asyncrona
	 * @param queueCapacity tamaño de la cola de espera por un hilo de ejecución disponible
	*/
	public SpringAsyncConfiguration(
		@Value("${cl.banchile.async.core-pool-size}") Integer corePoolSize,
		@Value("${cl.banchile.async.max-pool-size}") Integer maxPoolSize,
		@Value("${cl.banchile.async.queue-capacity}") Integer queueCapacity
	){
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = maxPoolSize;
		this.queueCapacity = queueCapacity;

	}

	/**
	 * Ejecutos asincrono
	 * con configuraciones ajustables desde properties
	 */
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.initialize();
		return executor;
	}

	/**
	 * Manejadore de excepciones bajo ejecuciones asíncronas
	 * Los detallado de errores
	 */
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) -> {
			log.error("Throwable Exception Class : {}", ex.getClass().getName());
			log.error("Throwable Exception Message : {}", ex.getMessage());
			log.error("Method name                 : {}", method.getName());
			for (Object param : params) {
				log.error("Parameter value             : {}", param);
			}
			log.error("Stacktrace: ", ex);
		};
	
	}
}
