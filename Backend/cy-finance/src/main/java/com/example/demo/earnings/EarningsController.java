package com.example.demo.earnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.util.Response;


import java.util.List;

@RestController
public class EarningsController {

    @Autowired
    EarningsRepository earningsRepository;

    private final String success = "{\"message\":\"success\"}";
    private final String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/earnings")
    List<Earnings> getAllEarnings(){return earningsRepository.findAll();}

    @GetMapping(path = "earnings/{id}")
    Earnings getEarningsById(@PathVariable int id){return earningsRepository.findById(id);}

    @PostMapping(path = "/earnings")
    String createEarnings(@RequestBody Earnings earnings){
        if (earnings == null)
            return failure;
        earningsRepository.save(earnings);
        return success;
    }


    @PutMapping(path = "/earnings/{id}")
    String updateEarnings(@PathVariable int id, @RequestBody Earnings request){
        Earnings earnings = earningsRepository.findById(id);
        if (earnings == null)
            return failure;
        earningsRepository.save(request);
        return success;
    }


    @DeleteMapping(path = "/earnings/{id}")
    String deleteEarnings(@PathVariable int id){
        Earnings earnings = earningsRepository.findById(id);
        if (earnings == null)
            return failure;
        earningsRepository.deleteById(id);
        return success;
    }
}
