package org.example.webquizapi.security;

import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository repository;

    public AppUserDetailsServiceImpl(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return new AppUserAdapter(user);
    }
}