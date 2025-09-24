package co.com.crediya.api.dto;

import co.com.crediya.model.loanrequest.LoanRequestSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta paginada de solicitudes de préstamo")
public class LoanRequestSummaryPageResponse {

    @Schema(description = "Contenido de la página")
    private List<LoanRequestSummary> content;

    @Schema(description = "Número de página actual", example = "0")
    private int page;

    @Schema(description = "Tamaño de página", example = "10")
    private int size;

    @Schema(description = "Cantidad total de elementos", example = "100")
    private long totalElements;
}
