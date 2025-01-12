package test.architect_711.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import test.architect_711.jwt.repository.SafePersonRepository;

@Service @RequiredArgsConstructor
public class PersonService implements UserDetailsService {
    private final SafePersonRepository safePersonRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return safePersonRepository.safeFindPersonByUsername(username);
    }

}
