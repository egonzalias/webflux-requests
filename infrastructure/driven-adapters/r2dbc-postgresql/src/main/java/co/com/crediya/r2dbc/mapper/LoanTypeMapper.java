package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.r2dbc.entity.LoanStatusEntity;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanTypeMapper {

    LoanType toModel(LoanTypeEntity entity);
    LoanTypeEntity toEntity(LoanType model);
}
