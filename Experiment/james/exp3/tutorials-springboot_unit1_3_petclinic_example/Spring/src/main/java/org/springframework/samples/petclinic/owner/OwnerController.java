/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @Modified By Tanmay Ghosh
 * @Modified By Vivek Bengre
 */
@RestController
class OwnerController {

    @Autowired
    OwnerRepository ownersRepository;

    private final Logger logger = LoggerFactory.getLogger(OwnerController.class);

    @RequestMapping(method = RequestMethod.POST, path = "/owners/new", params = {"username", "password"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> saveOwner(
            Owners owner,
            @RequestParam("username") String username,
            @RequestParam("password") String password)
    {
        if (username.equals("admin") && password.equals("password")){
            ownersRepository.save(owner);
            return ResponseEntity.status(HttpStatus.CREATED).body("New Owner "+ owner.getFirstName() + " Saved");
        } else if (username.equals("teapot")){
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Check status code");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please provide a valid username and password");
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/owners/new", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> saveOwnerUnauthenticated(Owners owner)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please provide a valid username and password");
    }

     // function just to create dummy data
    @RequestMapping(method = RequestMethod.GET, path = "/owner/create")
    public String createDummyData() {
        Owners o1 = new Owners(1, "John", "Doe", "404 Not found", "some numbers");
        Owners o2 = new Owners(2, "Jane", "Doe", "Its a secret", "you wish");
        Owners o3 = new Owners(3, "Some", "Pleb", "Right next to the Library", "515-345-41213");
        Owners o4 = new Owners(4, "Chad", "Champion", "Reddit memes corner", "420-420-4200");
        ownersRepository.save(o1);
        ownersRepository.save(o2);
        ownersRepository.save(o3);
        ownersRepository.save(o4);
        return "Successfully created dummy data";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/owners")
    public List<Owners> getAllOwners() {
        logger.info("Entered into Controller Layer");
        List<Owners> results = ownersRepository.findAll();
        logger.info("Number of Records Fetched:" + results.size());
        return results;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/owners/{ownerId}")
    public Optional<Owners> findOwnerById(@PathVariable("ownerId") int id) {
        logger.info("Entered into Controller Layer");
        Optional<Owners> results = ownersRepository.findById(id);
        return results;
    }

}
