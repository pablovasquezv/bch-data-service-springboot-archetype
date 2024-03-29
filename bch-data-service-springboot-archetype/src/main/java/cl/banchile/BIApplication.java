package cl.banchile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal de la ejecución
 * Inicia y mantiene la aplicación en ejecución e inicializa el contexto Spring y (auto)configuraciones
 */
@SpringBootApplication
@EnableFeignClients
public class BIApplication {
	/**
	 * Metodo main
	 * @param args argumentos
	 */
	public static void main(String[] args) {
		SpringApplication.run(BIApplication.class, args);
	}


}
