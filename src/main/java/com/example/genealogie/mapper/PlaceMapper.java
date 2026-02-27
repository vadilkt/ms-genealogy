package com.example.genealogie.mapper;

import com.example.genealogie.dto.PlaceDto;
import com.example.genealogie.model.Place;
import org.mapstruct.Mapper;
import org.springframework.lang.Nullable;

@Mapper(componentModel = "spring")
public abstract class PlaceMapper {

    @Nullable
    public PlaceDto toDto(@Nullable Place place) {
        if (place == null) {
            return null;
        }
        PlaceDto dto = new PlaceDto();
        dto.setId(place.getId());
        dto.setCity(place.getCity());
        dto.setCountry(place.getCountry());
        dto.setRegion(place.getRegion());
        return dto;
    }
}
