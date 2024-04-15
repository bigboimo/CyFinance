package com.example.demo;

import com.example.demo.users.User;
import com.example.demo.users.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CyFinanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CyFinanceApplication.class, args);
	}

	// Create Initial admin user and create normal user
	/**
	 *
	 * @param userRepository repository for the User entity
	 * Creates a commandLine runner to enter dummy data into the database
	 */
	@Bean
	CommandLineRunner initUser(UserRepository userRepository) {
		return args -> {
			User admin = new User("Admin", "test@email.com", "$2a$10$HU0kP0mnHf5xDlZ3wFg6o.e5xq6JVH4xIpERCoezOBzCttbr/9/MK", "admin");
			userRepository.save(admin);
			User user = new User("Test User", "test2@email.com", "$2a$10$zLzt5KZ.fI5SR4SOzl7ZCuqftmJVcqjPj2FGm3i89z0ePfkb02.mW", "user");
			userRepository.save(user);
		};
	}


}
