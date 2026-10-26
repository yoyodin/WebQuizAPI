package org.example.webquizapi.mapper;

import org.example.webquizapi.dto.AppUserDto;
import org.example.webquizapi.entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {
    // DTO -> Entity
    public AppUser dtoToEntity(AppUserDto dto) {
        return new AppUser(dto.getId(), dto.getEmail(), dto.getPassword(), null, null);
    }

    // Entity -> DTO
    public AppUserDto entityToDto(AppUser entity) {
        return new AppUserDto(entity.getId(), entity.getEmail(), entity.getPassword());
    }
}
