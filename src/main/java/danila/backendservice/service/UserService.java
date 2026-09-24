package danila.backendservice.service;

import danila.backendservice.dto.AuthRequestDto;
import danila.backendservice.dto.UserResponseDto;
import danila.backendservice.entity.User;
import danila.backendservice.exception.UserAlreadyExistException;
import danila.backendservice.repository.UserRepository;
import danila.backendservice.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_ALREADY_EXIST_BY_EMAIL = "User with this email already exist";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDto> getUserWithTasksList() {
        return userMapper.toDtoList(userRepository.findAllUserWithTasks());
    }

    public User create(AuthRequestDto authRequestDto) {
        if (userRepository.existsByEmail(authRequestDto.email())) {
            throw new UserAlreadyExistException(USER_ALREADY_EXIST_BY_EMAIL);
        }
        User user = new User(authRequestDto.email(), passwordEncoder.encode(authRequestDto.password()));
        return userRepository.save(user);
    }
}
