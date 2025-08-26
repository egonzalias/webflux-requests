package co.com.crediya.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanStatusDTO {
    private String code;
    private String description;
}