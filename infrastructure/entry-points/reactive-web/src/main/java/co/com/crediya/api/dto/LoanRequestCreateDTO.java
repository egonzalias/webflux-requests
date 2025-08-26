package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) //to copy an existing instance and update just need
public class LoanRequestCreateDTO {

    @NotBlank
    private String documentNumber;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    @Min(1)
    @Max(48)
    private Integer termInMonths;

    @NotBlank
    private String loanTypeCode;
}
