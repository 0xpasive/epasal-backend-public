package epasal.com.userservice.mapper;

import epasal.com.userservice.dtos.response.UserResponse;
import epasal.com.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    UserResponse entityToResponse(User user);
}
