package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.LoanRequestCreateDTO;
import co.com.crediya.api.dto.LoanRequestResponseDTO;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import co.com.crediya.model.loanrequest.LoanType;
import co.com.crediya.model.loanrequest.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

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
    @Mapping(source = "approved_monthly_debt", target = "approvedMonthlyDebt")
    LoanRequestResponseDTO toResponse(LoanRequestSummary loanRequest);

    @Mapping(source = "loanTypeCode", target = "loanTypeCode")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loanStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LoanRequest toModel(LoanRequestCreateDTO loanRequestCreateDTO);

    default PageResponse<LoanRequestResponseDTO> toPageResponse(PageResponse<LoanRequestSummary> page) {
        List<LoanRequestResponseDTO> content = page.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getPage(),
                page.getSize(),
                page.getTotalElements()
        );
    }

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
