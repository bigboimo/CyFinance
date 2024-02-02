package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Vivek Bengre
 */

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        System.out.println("This is just extras");
        return "Hello and welcome to COMS 309 THIS IS A FUN CLAS" + System.lineSeparator() + "It's a lot of work and practice but worth it";
    }

    @GetMapping("/69")
    public void easter() {
        System.out.println("Another easter buddy!");
    }
}
