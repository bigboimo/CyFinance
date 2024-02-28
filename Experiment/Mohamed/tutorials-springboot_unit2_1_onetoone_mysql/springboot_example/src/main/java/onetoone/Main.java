package onetoone;

import onetoone.Cars.CarsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import onetoone.Laptops.Laptop;
import onetoone.Laptops.LaptopRepository;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import onetoone.Cars.Cars;
import onetoone.Cars.CarsRepository;


/**
 * 
 * @author Vivek Bengre
 * 
 */ 

@SpringBootApplication
@EnableJpaRepositories
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Create 3 users with their machines
    /**
     * 
     * @param userRepository repository for the User entity
     * @param laptopRepository repository for the Laptop entity
     * @param carsRepository repository for the Cars entity
     * Creates a commandLine runner to enter dummy data into the database
     * As mentioned in User.java just associating the Laptop object with the User will save it into the database because of the CascadeType
     */
    @Bean
    CommandLineRunner initUser(UserRepository userRepository, LaptopRepository laptopRepository, CarsRepository carsRepository) {
        return args -> {
            User user1 = new User("John", "john@somemail.com");
            User user2 = new User("Jane", "jane@somemail.com");
            User user3 = new User("Justin", "justin@somemail.com");
            Laptop laptop1 = new Laptop( 2.5, 4, 8, "Lenovo", 300);
            Laptop laptop2 = new Laptop( 4.1, 8, 16, "Hp", 800);
            Laptop laptop3 = new Laptop( 3.5, 32, 32, "Dell", 2300);  
            user1.setLaptop(laptop1);
            user2.setLaptop(laptop2);
            user3.setLaptop(laptop3);            
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            Laptop laptop4 = new Laptop( 1.7, 4, 4, "Apple", 1000000);
            User user4 = new User("JOEY!", "JOEY!@gmail.com");
            Laptop laptop5 = new Laptop( 2.3, 4, 8, "HP", 600);
            user4.setLaptop(laptop5);
            User user5 = new User("Mo", "bigboimo@iastate.edu"); //This guy won't have laptop, spend all his money on ferrari! 
            user5.setId(420);
            Cars ferrari = new Cars();
            ferrari.setSpeed(9999);
            ferrari.setMpg(5);
            ferrari.setId(1);
            //Ferrari's are fast :)! But have bad Miles per gallon economy :(.. You're rich though so it's not a problem! :D
            user5.setCars(ferrari); //User 5 is our lucky one!
            //Let's give user 4 a car, a more reliable one.
            Cars hondaAccord = new Cars(180, 39);
            //Slower, but better fuel economy.
            user4.setCars(hondaAccord);
            userRepository.save(user4);
            userRepository.save(user5); //Must save to see on postman








        };
    }

}
