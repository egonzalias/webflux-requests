package co.com.crediya.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanTypeDTO {
    private String code;
    private String description;
}