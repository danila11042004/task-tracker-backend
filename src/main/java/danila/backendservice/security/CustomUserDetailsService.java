package danila.backendservice.security;

import danila.backendservice.entity.User;
import danila.backendservice.exception.UserNotFoundException;
import danila.backendservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final String USER_NOT_FOUND_BY_EMAIL = "User with this email not found";
    private static final String USER_NOT_FOUND_BY_ID = "User with this id not found";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        Assert.hasText(username, "Username not be null or empty");
        User user = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException(USER_NOT_FOUND_BY_EMAIL));
        return new UserPrincipal(user);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_ID));
        return new UserPrincipal(user);
    }
}
