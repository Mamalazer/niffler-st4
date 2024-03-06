package guru.qa.niffler.service;

import guru.qa.niffler.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class NifflerUserDetailsServiceFakeTest {

    private NifflerUserDetailsService nifflerUserDetailsService;
    private UserRepository userRepository;

    @BeforeEach
    void initMockRepository() {
        userRepository = new UserRepository.Fake();
        nifflerUserDetailsService = new NifflerUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername() {
        final String userName = "correct";
        final UserDetails correct = nifflerUserDetailsService.loadUserByUsername(userName);
        final List<SimpleGrantedAuthority> expectedAuthorities = Objects.requireNonNull(userRepository.findByUsername(userName))
                .getAuthorities().stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority().name()))
                .toList();

        assertEquals(
                "correct",
                correct.getUsername()
        );
        assertEquals(
                "test-pass",
                correct.getPassword()
        );
        assertEquals(
                expectedAuthorities,
                correct.getAuthorities()
        );

        assertTrue(correct.isAccountNonExpired());
        assertTrue(correct.isAccountNonLocked());
        assertTrue(correct.isCredentialsNonExpired());
        assertTrue(correct.isEnabled());
    }

    @Test
    void loadUserByUsernameNegayive() {
        final UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> nifflerUserDetailsService.loadUserByUsername("incorrect")
        );

        assertEquals(
                "Username: incorrect not found",
                exception.getMessage()
        );
    }
}