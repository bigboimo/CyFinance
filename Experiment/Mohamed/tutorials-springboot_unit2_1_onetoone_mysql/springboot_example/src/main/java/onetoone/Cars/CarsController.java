package onetoone.Cars;

import onetoone.Cars.Cars;
import onetoone.Cars.CarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
public class CarsController {

    @Autowired
    CarsRepository carsRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/Cars")
    List<Cars> getAllCars(){
        return carsRepository.findAll();
    }

    @GetMapping(path = "/Cars/{id}")
    Cars getCarsById(@PathVariable int id){
        return carsRepository.findById(id);
    }

    @PostMapping(path = "/Cars")
    String createCars(@RequestBody Cars cars){
        if (cars == null)
            return failure;
        carsRepository.save(cars);
        return success;
    }

    @PutMapping(path = "/Cars/{id}")
    Cars updateCars(@PathVariable int id, @RequestBody Cars request){
        Cars cars = carsRepository.findById(id);
        if(cars == null)
            return null;
        carsRepository.save(request);
        return carsRepository.findById(id);
    }

    @DeleteMapping(path = "/Cars/{id}")
    String deleteCars(@PathVariable int id){
        carsRepository.deleteById(id);
        return success;
    }

}
