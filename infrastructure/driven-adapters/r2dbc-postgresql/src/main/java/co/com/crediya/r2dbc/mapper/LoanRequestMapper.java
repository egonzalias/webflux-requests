package co.com.crediya.r2dbc.mapper;


import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.r2dbc.dto.LoanRequestExtendedDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanRequestMapper {

        @Mapping(target = "loanStatus", expression = "java(mapToLoanStatus(entity))")
        @Mapping(target = "loanTypeCode", expression = "java(mapToLoanType(entity))")
        @Mapping(source = "document_number", target = "documentNumber")
        @Mapping(source = "term_months", target = "termInMonths")
        @Mapping(source = "created_at", target = "createdAt")
        LoanRequest toDomain(LoanRequestExtendedDTO entity);

        @Mapping(target = "loanStatus", expression = "java(mapToLoanStatus(entity))")
        @Mapping(target = "loanTypeCode", expression = "java(mapToLoanType(entity))")
        @Mapping(source = "document_number", target = "documentNumber")
        @Mapping(source = "term_months", target = "termInMonths")
        @Mapping(source = "created_at", target = "createdAt")
        LoanRequestSummary toDomainExtend(LoanRequestExtendedDTO entity);

        /*@Mapping(target = "status", source = "loanStatus.id")
        @Mapping(target = "loanType", source = "loanTypeCode.id")
        LoanRequestEntity toEntity(LoanRequest domain);*/

        // Auxiliar methods used in the expressions
        default LoanStatus mapToLoanStatus(LoanRequestExtendedDTO dto) {
                return LoanStatus.builder()
                        .id(dto.getStatus_id())
                        .code(dto.getStatus_code())
                        .description(dto.getStatus_description())
                        .build();
        }

        default LoanType mapToLoanType(LoanRequestExtendedDTO dto) {
                return LoanType.builder()
                        .id(dto.getLoan_type_id())
                        .name(dto.getLoan_type_name())
                        .description(dto.getLoan_type_description())
                        .minimumAmount(dto.getLoan_type_minimum_amount())
                        .maximumAmount(dto.getLoan_type_maximum_amount())
                        .interestRate(dto.getLoan_type_interest_rate())
                        .automaticValidation(dto.isLoan_type_automatic_validation())
                        .build();
        }



}
