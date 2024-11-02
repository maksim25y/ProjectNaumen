package ru.mudan.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.mudan.services.auth.MyUserDetailsService;

@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {

    private final MyUserDetailsService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        UserDetails userFromDB = userService.loadUserByUsername(email);

        var passwordDB = userFromDB.getPassword();

        if (!passwordEncoder.matches(password, passwordDB)) {
            throw new AuthenticationServiceException("Пароль неверный!");
        }

        return new UsernamePasswordAuthenticationToken(userFromDB.getUsername(),
                userFromDB.getPassword(), userFromDB.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
