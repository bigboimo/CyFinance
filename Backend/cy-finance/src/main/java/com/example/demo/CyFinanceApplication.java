package com.example.demo;

import com.example.demo.users.User;
import com.example.demo.users.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication // This annotation encompasses @Configuration, @EnableAutoConfiguration, and @ComponentScan
@EnableJpaRepositories // This is actually redundant when using @SpringBootApplication unless you need specific customization
public class CyFinanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CyFinanceApplication.class, args);
	}

	/**
	 * Creates a command-line runner to enter dummy data into the database.
	 * This is executed on startup.
	 *
	 * @param userRepository repository for the User entity
	 * @return CommandLineRunner
	 */
	@Bean
	CommandLineRunner initUser(UserRepository userRepository) {
		return args -> {
			User admin = new User("Admin", "admin@email.com", "$2a$10$HU0kP0mnHf5xDlZ3wFg6o.e5xq6JVH4xIpERCoezOBzCttbr/9/MK", "admin");
			userRepository.save(admin);
			User user = new User("Test User", "user@email.com", "$2a$10$zLzt5KZ.fI5SR4SOzl7ZCuqftmJVcqjPj2FGm3i89z0ePfkb02.mW", "user");
			userRepository.save(user);
		};
	}
}
