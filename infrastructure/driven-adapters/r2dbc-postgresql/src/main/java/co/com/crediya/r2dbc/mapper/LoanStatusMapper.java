package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.r2dbc.entity.LoanStatusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanStatusMapper {

    LoanStatus toModel(LoanStatusEntity entity);
    LoanStatusEntity toEntity(LoanStatus model);
}
