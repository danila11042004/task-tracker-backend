package danila.backendservice.controller;

import danila.backendservice.dto.CurrentUserResponseDto;
import danila.backendservice.security.UserPrincipal;
import danila.backendservice.util.EndpointConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CurrentUserController {

  @GetMapping(value = EndpointConstants.CURRENT_USER_ENDPOINT)
  @ResponseStatus(HttpStatus.OK)
  public CurrentUserResponseDto getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
    return new CurrentUserResponseDto(userPrincipal.getId(), userPrincipal.getUsername());
  }
}
