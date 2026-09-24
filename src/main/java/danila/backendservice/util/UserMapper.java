package danila.backendservice.util;

import danila.backendservice.dto.UserResponseDto;
import danila.backendservice.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserResponseDto> toDtoList(List<User> userList);
}
