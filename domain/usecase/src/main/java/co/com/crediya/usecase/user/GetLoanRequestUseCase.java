package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ValidationException;
import co.com.crediya.model.loanrequest.LoanRequestSummary;
import co.com.crediya.model.loanrequest.LoanStatus;
import co.com.crediya.model.loanrequest.PageResponse;
import co.com.crediya.model.loanrequest.gateways.LoanRequestRepository;
import co.com.crediya.model.loanrequest.gateways.LoanStatusRepository;
import co.com.crediya.model.loanrequest.gateways.LoggerService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetLoanRequestUseCase {

    private final LoanStatusRepository loanStatusRepository;
    private final LoanRequestRepository repository;
    private final LoggerService logger;

    public Mono<PageResponse<LoanRequestSummary>> getLoanRequestsByStatus(List<String> codeStatuses, int page, int size) {
        int offset = page * size;

        return getLoanStatuses(codeStatuses)
                .flatMap(statuses -> {
                    List<Long> statusIds = statuses.stream()
                            .map(LoanStatus::getId)
                            .collect(Collectors.toList());

                    Mono<List<LoanRequestSummary>> contentMono = repository.findLoanRequestsByStatusIn(statusIds, size, offset)
                            .collectList();

                    Mono<Long> totalMono = repository.countLoanRequestByStatusIn(statusIds);

                    return Mono.zip(contentMono, totalMono)
                            .map(tuple -> new PageResponse<>(
                                    tuple.getT1(),
                                    page,
                                    size,
                                    tuple.getT2()
                            ));
                });

        /*return loanStatusRepository.findStatusByCodes(codeStatuses)
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
                });*/



    }

    private Mono<List<LoanStatus>> getLoanStatuses(List<String> codeStatuses){
        return loanStatusRepository.findStatusByCodes(codeStatuses)
                .switchIfEmpty(Mono.error(new ValidationException(List.of("Ninguno de los estados proporcionados es válido."))))
                .collectList();
    }

}
