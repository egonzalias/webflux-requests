package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.loanrequest.User;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toModel(UserEntity entity);
    UserEntity toEntity(User model);
}
