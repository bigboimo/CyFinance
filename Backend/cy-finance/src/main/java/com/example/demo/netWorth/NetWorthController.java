package com.example.demo.netWorth;

import com.example.demo.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NetWorthController {

    @Autowired
    NetWorthRepository netWorthRepository;

    @GetMapping(path = "/networth")
    List<NetWorth> getNetWorth() {
        return netWorthRepository.findAll();
    }

    @GetMapping(path = "/networth/{id}")
    NetWorth getNetWorthById(@PathVariable int id) {
        return netWorthRepository.findById(id);
    }

    @PostMapping(path = "/networth")
    ResponseEntity<Response<String>> createNetWorth(@RequestBody NetWorth netWorth) {
        Response<String> response = new Response<>();
        if (netWorth == null) {
            response.put("message", "No net worth specified");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Net worth created");
            netWorthRepository.save(netWorth);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping(path = "/networth")
    ResponseEntity<Response<String>> modifyNetWorth(@RequestBody NetWorth netWorth) {
        Response<String> response = new Response<>();
        if (netWorth == null) {
            response.put("message", "No net worth provided");
            return ResponseEntity.ok(response);
        } else if (netWorthRepository.findById(netWorth.getId()) == null) {
            response.put("message", "Net worth doesn't exist");
            return ResponseEntity.ok(response);
        } else {
            if (netWorth.getAssets() == -1)
                netWorth.setAssets(netWorthRepository.findById(netWorth.getId()).getAssets());
            if (netWorth.getLiabilities() == -1)
                netWorth.setLiabilities(netWorthRepository.findById(netWorth.getId()).getLiabilities());
            netWorthRepository.save(netWorth);
            response.put("message", "Net worth modified");
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping(path = "/networth/{id}")
    ResponseEntity<Response<String>> deleteNetWorth(@PathVariable int id) {
        Response<String> response = new Response<>();
        netWorthRepository.deleteById(id);
        response.put("message", "Net worth deleted");
        return ResponseEntity.ok(response);
    }

}
