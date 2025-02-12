package hu.davidder.webapp.core.base.pet.converter;

import hu.davidder.webapp.core.base.pet.enums.PetStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PetStatusConverter implements AttributeConverter<PetStatus, String> {

    @Override
    public String convertToDatabaseColumn(PetStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getName();
    }

    @Override
    public PetStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return PetStatus.fromName(dbData);
    }
}
