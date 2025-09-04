package co.com.crediya.api;


import co.com.crediya.api.dto.LoanRequestCreateDTO;
import co.com.crediya.api.dto.LoanRequestResponseDTO;
import co.com.crediya.api.dto.PaginationStatusParams;
import co.com.crediya.api.mapper.LoanRequestDTOMapper;
import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.JwtUserInfo;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.PageResponse;
import co.com.crediya.usecase.user.GetLoanRequestUseCase;
import co.com.crediya.usecase.user.LoanRequestUseCase;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanRequestUseCase loanRequestUseCase;
    private final GetLoanRequestUseCase getLoanRequestUseCase;
    private final LoanRequestDTOMapper loanRequestDTOMapper;
    private final Validator validator;

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

                            return loanRequestUseCase.loanRequest(domainRequest).then(ServerResponse.status(HttpStatus.CREATED).build());
                        })
                );
    }

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
