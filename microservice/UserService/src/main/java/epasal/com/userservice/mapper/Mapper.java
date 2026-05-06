package epasal.com.userservice.mapper;

import epasal.com.userservice.dtos.requests.CreateUserRequest;
import epasal.com.userservice.dtos.response.RegisterResponse;
import epasal.com.userservice.entity.User;
import org.mapstruct.NullValuePropertyMappingStrategy;

@org.mapstruct.Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface Mapper {
    User requestToEntity(CreateUserRequest createUserRequest);

    RegisterResponse entityToResponse(User user);
}
