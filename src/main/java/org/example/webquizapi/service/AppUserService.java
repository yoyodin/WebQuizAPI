package org.example.webquizapi.service;

import org.example.webquizapi.dto.AppUserDto;
import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.mapper.AppUserMapper;
import org.example.webquizapi.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository,
                          AppUserMapper appUserMapper,
                          PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUserDto save(AppUserDto dto) {
        AppUser appUser = appUserMapper.dtoToEntity(dto);

        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        appUser = appUserRepository.save(appUser);

        return appUserMapper.entityToDto(appUser);
    }

    public List<AppUserDto> getAllUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(appUserMapper::entityToDto)
                .toList();
    }
}
