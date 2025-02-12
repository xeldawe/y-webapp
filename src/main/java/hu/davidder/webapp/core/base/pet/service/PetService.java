package hu.davidder.webapp.core.base.pet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import hu.davidder.webapp.core.base.pet.entity.Pet;
import hu.davidder.webapp.core.base.pet.repository.PetRepository;

@Service
public class PetService {

	private static final Logger logger = LoggerFactory.getLogger(PetService.class);
	
    @Autowired
    private PetRepository petRepository;
    
    @Cacheable(value = "pets", unless = "#result == null or #result.size()==0")
    public Iterable<Pet> getAllPets() {
    	logger.info("Fetching pets from the database");
        return petRepository.findAll();
    }
}
