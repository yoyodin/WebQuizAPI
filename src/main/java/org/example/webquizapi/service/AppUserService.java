package org.example.webquizapi.service;

import org.example.webquizapi.dto.AppUserDto;

import java.util.List;

public interface AppUserService {
    AppUserDto save(AppUserDto dto);

    List<AppUserDto> getAllUsers();
}
