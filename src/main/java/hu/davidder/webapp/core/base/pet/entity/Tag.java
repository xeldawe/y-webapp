package hu.davidder.webapp.core.base.pet.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hu.davidder.webapp.core.base.EntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
public class Tag extends EntityBase {
	
	public Tag() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Tag(String name) {
		this.name = name;
	}

	private String name;

	@OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<PetXTag> petTags;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PetXTag> getPetTags() {
		return petTags;
	}

	public void setPetTags(List<PetXTag> petTags) {
		this.petTags = petTags;
	}

	
}
