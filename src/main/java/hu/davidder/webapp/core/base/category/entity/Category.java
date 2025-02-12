package hu.davidder.webapp.core.base.category.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hu.davidder.webapp.core.base.EntityBase;
import hu.davidder.webapp.core.base.pet.entity.PetXCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Category extends EntityBase {
    private String name;
    
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<PetXCategory> petCategories;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


}
