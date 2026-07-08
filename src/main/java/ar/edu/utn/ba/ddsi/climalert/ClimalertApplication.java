package ar.edu.utn.ba.ddsi.climalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(excludeName = {
		"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
		"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
		"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
		"org.springframework.boot.jpa.autoconfigure.HibernateJpaAutoConfiguration",
		"org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
		"org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
		"org.springframework.boot.data.jpa.autoconfigure.JpaRepositoriesAutoConfiguration"
})
public class ClimalertApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClimalertApplication.class, args);
	}

}



