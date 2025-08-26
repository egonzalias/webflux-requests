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
    LoanRequest toModel(LoanRequestCreateDTO loanRequestCreateDTO);


    default LoanType map(String code) {
        if (code == null) {
            return null;
        }
        LoanType loanType = new LoanType();
        loanType.setCode(code);
        return loanType;
    }

    default String map(LoanType loanType) {
        if (loanType == null) {
            return null;
        }
        return loanType.getCode();
    }
}
