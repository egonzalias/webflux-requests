package co.com.crediya.api;


import co.com.crediya.api.dto.LoanRequestCreateDTO;
import co.com.crediya.api.dto.PaginationStatusParams;
import co.com.crediya.api.mapper.LoanRequestDTOMapper;
import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.usecase.user.GetLoanRequestUseCase;
import co.com.crediya.usecase.user.LoanRequestUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        return serverRequest
                .bodyToMono(LoanRequestCreateDTO.class)
                .doOnNext(this::validate)
                .map(loanRequestDTOMapper::toModel)
                .flatMap(loanRequestUseCase::loanRequest)
                .then(ServerResponse.status(HttpStatus.CREATED).build());
    }

    public Mono<ServerResponse> getLoanRequestsByType(ServerRequest serverRequest) {
        return validateQueryParams(serverRequest)
                .flatMap(params ->
                    getLoanRequestUseCase.getLoanRequestsByStatus(params.codeStatus(), params.page(), params.size() )
                            .map(loanRequestDTOMapper::toResponse)
                            .collectList()
                            .flatMap(list -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(list))
                );
    }

    private void validate(LoanRequestCreateDTO loanRequestCreateDTO) {
        BindingResult errors = new BeanPropertyBindingResult(loanRequestCreateDTO, LoanRequestCreateDTO.class.getName());
        validator.validate(loanRequestCreateDTO, errors);
        if (errors.hasErrors()) {
            List<String> messages = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationException(messages);
        }
    }

    private Mono<PaginationStatusParams> validateQueryParams(ServerRequest request){
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

        return Mono.just(new PaginationStatusParams(statusStr, page, size));
    }
}
