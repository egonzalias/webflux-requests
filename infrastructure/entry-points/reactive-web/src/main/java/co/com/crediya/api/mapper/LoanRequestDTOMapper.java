package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.LoanRequestCreateDTO;
import co.com.crediya.api.dto.LoanRequestResponseDTO;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
/**
 * Default methods allow defining custom conversions
 * directly within the mapper interface.
 *
 * This enables MapStruct to use these implementations
 * without requiring additional helper classes,
 * keeping the code simple and centralized.
 * AI
 */
public interface LoanRequestDTOMapper {
    @Mapping(source = "loanTypeCode", target = "loanType")
    LoanRequestResponseDTO toResponse(LoanRequest loanRequest);

    @Mapping(source = "loanTypeCode", target = "loanTypeCode")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loanStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LoanRequest toModel(LoanRequestCreateDTO loanRequestCreateDTO);


    default LoanType map(String code) {
        if (code == null) {
            return null;
        }
        LoanType loanType = new LoanType();
        loanType.setName(code);
        return loanType;
    }

    default String map(LoanType loanType) {
        if (loanType == null) {
            return null;
        }
        return loanType.getName();
    }
}
