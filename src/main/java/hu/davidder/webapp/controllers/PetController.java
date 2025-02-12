package hu.davidder.webapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import hu.davidder.webapp.core.base.pet.entity.Pet;
import hu.davidder.webapp.core.base.pet.repository.PetRepository;

@RestController
@RequestMapping("pet")
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PetController {

	@Lazy
	@Autowired
	private PetRepository petCrudRepository;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)    
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
        return ResponseEntity.ok(petCrudRepository.save(pet));
    }
    
    @PostMapping("bulk")
    public ResponseEntity<Iterable<Pet>> createPets(@RequestBody List<Pet> pets) {
        return ResponseEntity.ok(petCrudRepository.saveAll(pets));
    }
	
}
