package org.example.webquizapi.service;

import org.example.webquizapi.dto.AppUserDto;
import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.mapper.AppUserMapper;
import org.example.webquizapi.repository.AppUserRepository;
import org.example.webquizapi.service.impl.AppUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceImplTest {
    private final AppUser mockAppUser = new AppUser(1L, "test@test.com", "password", null, null);
    private final AppUserDto mockDto = new AppUserDto(1L, "test@test.com", "password");

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserMapper appUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Test
    void save_shouldReturnAppUserDto() {
        when(appUserMapper.dtoToEntity(any(AppUserDto.class))).thenReturn(mockAppUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(mockAppUser);
        when(appUserMapper.entityToDto(any(AppUser.class))).thenReturn(mockDto);

        var savedUser = appUserService.save(mockDto);

        assertThat(savedUser.getId()).isEqualTo(mockDto.getId());
        assertThat(savedUser.getEmail()).isEqualTo(mockDto.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(mockDto.getPassword());

        verify(appUserMapper).entityToDto(any());
        verify(appUserMapper).entityToDto(any());
        verify(appUserRepository).save(any());
        verify(passwordEncoder).encode(any());
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(appUserRepository.findAll()).thenReturn(List.of(mockAppUser));
        when(appUserMapper.entityToDto(any(AppUser.class))).thenReturn(mockDto);

        var users = appUserService.getAllUsers();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.getFirst().getEmail()).isEqualTo(mockDto.getEmail());

        verify(appUserRepository).findAll();
        verify(appUserMapper).entityToDto(any());
    }
}
