package sistema.reservas_restaurante_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReservasRestauranteApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservasRestauranteApiApplication.class, args);
	}

}
