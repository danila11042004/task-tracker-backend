package danila.backendservice.service;

import danila.backendservice.dto.AuthRequestDto;
import danila.backendservice.entity.User;
import danila.backendservice.kafka.message.EmailMessage;
import danila.backendservice.security.UserPrincipal;
import danila.backendservice.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final String topic;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final OutboxEventService outboxEventService;

    public AuthenticationService(
            @Value("${kafka.topics.sending-email}") String topic,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            OutboxEventService outboxEventService) {
        this.topic = topic;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.outboxEventService = outboxEventService;
    }

    @Transactional
    public String register(AuthRequestDto authRequestDto) {
        User user = userService.create(authRequestDto);
        outboxEventService.create(topic, new EmailMessage(UUID.randomUUID(), user.getEmail(),
                "Welcome to task-tracker", "Registration successful"));
        UserPrincipal userPrincipal = new UserPrincipal(user);
        return jwtService.generateJwtToken(userPrincipal);
    }

    public String login(AuthRequestDto authRequestDto) {
        String normalizedEmail = authRequestDto.email().strip().toLowerCase(Locale.ROOT);
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(normalizedEmail, authRequestDto.password()));
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Assert.notNull(userPrincipal, "Authenticated principal cannot be null");
        return jwtService.generateJwtToken(userPrincipal);
    }
}
