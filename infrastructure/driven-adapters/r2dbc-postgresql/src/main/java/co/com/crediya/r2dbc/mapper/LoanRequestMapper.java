package co.com.crediya.r2dbc.mapper;


import co.com.crediya.model.loanrequest.*;
import co.com.crediya.r2dbc.dto.LoanRequestExtendedDTO;
import co.com.crediya.r2dbc.entity.LoanRequestEntity;
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
        @Mapping(source = "first_name", target = "firstName")
        @Mapping(source = "last_name", target = "lastName")
        LoanRequestSummary toDomainExtend(LoanRequestExtendedDTO entity);

        /*@Mapping(target = "status", source = "loanStatus.id")
        @Mapping(target = "loanType", source = "loanTypeCode.id")
        LoanRequestEntity toEntity(LoanRequest domain);*/

        @Mapping(target = "loanStatus", expression = "java(mapLoanStatus(domain.getStatus()))")
        @Mapping(target = "loanTypeCode", expression = "java(mapLoanType(domain.getLoanType()))")
        LoanRequest toDomain(LoanRequestEntity domain);

        // Mapeo de Long -> LoanStatus
        default LoanStatus mapLoanStatus(Long id) {
                return id == null ? null : LoanStatus.builder().id(id).build();
        }

        // Mapeo de Long -> LoanType
        default LoanType mapLoanType(Long id) {
                return id == null ? null : LoanType.builder().id(id).build();
        }

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
