package danila.backendservice.controller;

import danila.backendservice.dto.AuthRequestDto;
import danila.backendservice.service.AuthenticationService;
import danila.backendservice.util.EndpointConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = EndpointConstants.CURRENT_USER_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registerWithJsonData(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return authentication(authRequestDto, authenticationService::register);
    }

    @PostMapping(value = EndpointConstants.CURRENT_USER_ENDPOINT,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> registerWithFormData(@Valid @ModelAttribute AuthRequestDto authRequestDto) {
        return authentication(authRequestDto, authenticationService::register);
    }

    @PostMapping(value = EndpointConstants.LOGIN_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> loginWithJsonData(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return authentication(authRequestDto, authenticationService::login);
    }

    @PostMapping(value = EndpointConstants.LOGIN_ENDPOINT,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> loginWithFormData(@Valid @ModelAttribute AuthRequestDto authRequestDto) {
        return authentication(authRequestDto, authenticationService::login);
    }

    private ResponseEntity<Void> authentication(AuthRequestDto authRequestDto, Function<AuthRequestDto,
            String> authenticationAction) {
        String token = authenticationAction.apply(authRequestDto);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
    }
}
