package co.com.crediya.api;


import co.com.crediya.api.dto.LoanRequestCreateDTO;
import co.com.crediya.api.dto.LoanRequestSummaryPageResponse;
import co.com.crediya.api.dto.LoanRequestUpdateStatusDTO;
import co.com.crediya.api.dto.PaginationStatusParams;
import co.com.crediya.api.exception.ErrorResponse;
import co.com.crediya.api.mapper.LoanRequestDTOMapper;
import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.JwtUserInfo;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.usecase.user.GetLoanRequestUseCase;
import co.com.crediya.usecase.user.CreateLoanRequestUseCase;
import co.com.crediya.usecase.user.UpdateLoanRequestUseCase;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateLoanRequestUseCase createLoanRequestUseCase;
    private final GetLoanRequestUseCase getLoanRequestUseCase;
    private final UpdateLoanRequestUseCase updateLoanRequestUseCase;
    private final LoanRequestDTOMapper loanRequestDTOMapper;
    private final Validator validator;
    @Value("${aws.queue-loan-status-update}")
    private String queueLoanStatusUpdate;
    @Value("${aws.queue-loan-auto-evaluation}")
    private String queueLoanAutoEvaluation;
    @Value("${aws.queue-loan-approved-reports}")
    private String queueLoanApprovedReports;

    @Operation(
            summary = "Crea una nueva solicitud de préstamo",
            description = "Crea una solicitud de préstamo si el cliente es válido y los datos están correctos.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanRequestCreateDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Solicitud creada exitosamente (sin contenido)"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación o datos incorrectos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Acceso prohibido"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado o rol invalido para realizar esta accion."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public Mono<ServerResponse> loanRequest(ServerRequest serverRequest) {

        Mono<LoanRequestCreateDTO> bodyMono = serverRequest.bodyToMono(LoanRequestCreateDTO.class);

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> (JwtUserInfo) auth.getDetails())
                .flatMap(userInfo ->
                        bodyMono.flatMap(dto -> {
                            if (!dto.getDocumentNumber().equals(userInfo.getDocumentNumber())) {
                                return Mono.error(new ValidationException(List.of("El cliente solo puede crear solicitudes para si mismo.")));
                            }

                            validate(dto);
                            LoanRequest domainRequest = loanRequestDTOMapper.toModel(dto);

                            return createLoanRequestUseCase.loanRequest(domainRequest, queueLoanAutoEvaluation).then(ServerResponse.status(HttpStatus.CREATED).build());
                        })
                );
    }

    @Operation(
            summary = "Obtiene solicitudes de préstamo por estado y paginación",
            description = "Filtra solicitudes por estado (`status`) y permite paginar con `page` y `size`. El parámetro `status` es obligatorio y puede incluir múltiples valores.",
            parameters = {
                    @Parameter(
                            name = "status",
                            description = "Lista de estados a filtrar (ej. PENDING, APPROVED)",
                            in = ParameterIn.QUERY,
                            required = true,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    ),
                    @Parameter(
                            name = "page",
                            description = "Número de página (debe ser entero >= 0)",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "Tamaño de página (entero > 0)",
                            in = ParameterIn.QUERY,
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "10", minimum = "1")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista paginada de solicitudes de préstamo",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoanRequestSummaryPageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos o estado faltante",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Acceso prohibido"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado o rol invalido para realizar esta accion."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public Mono<ServerResponse> getLoanRequestsByType(ServerRequest serverRequest) {
        return validateQueryParams(serverRequest)
                .flatMap(params ->
                    getLoanRequestUseCase.getLoanRequestsByStatus(params.codeStatuses(), params.page(), params.size() )
                            .map(loanRequestDTOMapper::toPageResponse)
                            .flatMap(responseDTO ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(responseDTO)
                            )
                );
    }

    private Mono<Void> validate(LoanRequestCreateDTO loanRequestCreateDTO) {
        BindingResult errors = new BeanPropertyBindingResult(loanRequestCreateDTO, LoanRequestCreateDTO.class.getName());
        validator.validate(loanRequestCreateDTO, errors);
        if (errors.hasErrors()) {
            List<String> messages = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return Mono.error(new ValidationException(messages));
        }
        return Mono.empty();
    }

    @Operation(
            summary = "Actualiza el estado de una solicitud de préstamo",
            description = "Permite actualizar el estado de una solicitud de préstamo identificada por su ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID de la solicitud de préstamo a actualizar",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string", example = "123")
                    )
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Nuevo estado de la solicitud",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanRequestUpdateStatusDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Estado actualizado correctamente (sin contenido)"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación o datos incorrectos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public Mono<ServerResponse> updateLoanStatus(ServerRequest serverRequest) {
        String idLoan = serverRequest.pathVariable("id");
        if(idLoan.isBlank()){
            return Mono.error(new ValidationException(List.of("El ID de la solicitud es requerido en la URL.")));
        }
        return serverRequest
                .bodyToMono(LoanRequestUpdateStatusDTO.class)
                .map(loanRequestDTOMapper::toModelUpdateStatus)
                .map(model -> {
                    model.setId(Long.valueOf(idLoan));
                    return  model;
                })
                .flatMap(model -> updateLoanRequestUseCase.updateLoanStatus(model, queueLoanStatusUpdate, queueLoanApprovedReports))
                .then(ServerResponse.status(HttpStatus.NO_CONTENT).build());
    }

    private Mono<PaginationStatusParams> validateQueryParams(ServerRequest request){

        List<String> statuses = request.queryParams().getOrDefault("status", List.of());
        if (statuses.isEmpty()) {
            return Mono.error(new ValidationException(List.of("Debe enviar por lo menos un estado para generar el reporte")));
        }

        String statusStr = request.queryParam("status").orElse("0");
        String pageStr = request.queryParam("page").orElse("0");
        String sizeStr = request.queryParam("size").orElse("10");

        int page;
        int size;

        try {
            page = Integer.parseInt(pageStr);
            size = Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            return Mono.error(new ValidationException(List.of("'page' y 'size' deben ser números enteros válidos")));
        }

        if (page < 0 || size <= 0 ) {
            return Mono.error(new ValidationException(List.of("'page' debe ser >= 0 , 'size' > 0")));
        }

        return Mono.just(new PaginationStatusParams(statuses, page, size));
    }
}
