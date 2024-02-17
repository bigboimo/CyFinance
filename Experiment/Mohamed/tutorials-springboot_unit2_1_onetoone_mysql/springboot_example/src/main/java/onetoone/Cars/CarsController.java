package onetoone.Cars;

import onetoone.Cars.Cars;
import onetoone.Cars.CarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
