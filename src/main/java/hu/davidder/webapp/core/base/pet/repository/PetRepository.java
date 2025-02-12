package hu.davidder.webapp.core.base.pet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.davidder.webapp.core.base.pet.entity.Pet;

@Repository
public interface PetRepository extends CrudRepository<Pet, Long> {

}