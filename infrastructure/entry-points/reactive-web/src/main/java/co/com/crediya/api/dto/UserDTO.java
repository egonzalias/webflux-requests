package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {
    private Long id;
    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String first_name;
    @NotBlank(message = "Los apellidos son obligatorios")
    private String last_name;
    private LocalDate birth_date;
    private String address;
    private String phone;
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido", regexp = ".+@.+\\..+")
    private String email;
    @NotNull(message = "El salario base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El salario base debe ser mayor o igual a 0")
    @DecimalMax(value = "15000000", inclusive = true, message = "El salario base no puede superar 15,000,000")
    private BigDecimal base_salary;
}
