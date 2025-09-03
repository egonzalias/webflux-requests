package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequest;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetLoanRequestUseCase {

    private final LoanStatusRepository loanStatusRepository;
    private final LoanRequestRepository repository;
    private final LoggerService logger;

    public Flux<LoanRequestSummary> getLoanRequestsByStatus(List<String> codeStatuses, int page, int size) {
        int offset = page * size;

        return loanStatusRepository.findStatusByCodes(codeStatuses)
                // Collect all LoanStatus elements into a single list inside a Mono
                // This transforms Flux<LoanStatus> into Mono<List<LoanStatus>>
                .collectList()
                // Use flatMapMany because we now have a Mono<List<LoanStatus>>
                // and we want to transform it into a Flux<Loan> (a stream of results)
                .flatMapMany(statuses -> {
                            if (statuses.isEmpty()) {
                                return Flux.error(new ValidationException(
                                        List.of("Ninguno de los estados proporcionados es válido.")
                                ));
                            }

                            List<Long> statusIds = statuses.stream()
                                    .map(LoanStatus::getId)
                                    .collect(Collectors.toList());

                            return repository.findByStatusIn(statusIds, size, offset);
                });

        /*return loanStatusRepository.findStatusByCode(codeStatus)
                .switchIfEmpty(Mono.error(new ValidationException(
                        List.of("El estado '" + codeStatus + "' es incorrecto o no existe en la base de datos.")
                )))
                .flatMapMany(status ->
                        repository.findByStatus(status.getId(), size, offset)
                );*/
    }


}
