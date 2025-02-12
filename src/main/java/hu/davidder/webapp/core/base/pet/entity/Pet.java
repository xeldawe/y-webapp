package hu.davidder.webapp.core.base.pet.entity;

import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import hu.davidder.webapp.core.base.EntityBase;
import hu.davidder.webapp.core.base.pet.converter.PetStatusConverter;
import hu.davidder.webapp.core.base.pet.enums.PetStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
public class Pet extends EntityBase {
    
    @Column(nullable = false)
    private String name;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    private List<String> photoUrls;
    
    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @JsonManagedReference
    private List<PetXTag> tags;
    
    @Column(nullable = false)
    @Convert(converter = PetStatusConverter.class)
    private PetStatus petStatus;
    
	@OneToMany(mappedBy = "pet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	  @Fetch(FetchMode.JOIN)
	private List<PetXCategory> petCategories;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPhotoUrls() {
		return photoUrls;
	}

	public void setPhotoUrls(List<String> photoUrls) {
		this.photoUrls = photoUrls;
	}

	public List<PetXTag> getTags() {
		return tags;
	}

	public void setTags(List<PetXTag> tags) {
		this.tags = tags;
	}

	public PetStatus getPetStatus() {
		return petStatus;
	}

	public void setStatus(PetStatus petStatus) {
		this.petStatus = petStatus;
	}

	public List<PetXCategory> getPetCategories() {
		return petCategories;
	}

	public void setPetCategories(List<PetXCategory> petCategories) {
		this.petCategories = petCategories;
	}

	public void setPetStatus(PetStatus petStatus) {
		this.petStatus = petStatus;
	}
    
}

