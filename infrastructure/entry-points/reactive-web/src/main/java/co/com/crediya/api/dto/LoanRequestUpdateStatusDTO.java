package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) //to copy an existing instance and update just need
public class LoanRequestUpdateStatusDTO {
    @NotBlank(message = "El estado es obligatorio")
    private String status;
}
