package co.com.crediya.r2dbc.adapter;

import co.com.crediya.r2dbc.dto.LoanRequestExtendedDTO;
import co.com.crediya.r2dbc.entity.LoanRequestEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface RequestReactiveRepository extends ReactiveCrudRepository<LoanRequestEntity, String>, ReactiveQueryByExampleExecutor<LoanRequestEntity> {

    @Query("""
    SELECT
          lr.id,
          lr.document_number,
          lr.amount,
          lr.term_months,
          lt.id as loan_type_id,
          lt.name as loan_type_name,
          lt.description as loan_type_description,
          lt.minimum_amount as loan_type_minimum_amount,
          lt.maximum_amount as loan_type_maximum_amount,
          lt.interest_rate as loan_type_interest_rate,
          lt.automatic_validation as loan_type_automatic_validation,
          ls.id AS status_id,
          ls.code AS status_code,
          ls.description AS status_description,
          lr.created_at
        FROM loan_requests lr
        JOIN loan_types lt ON lr.loan_type_id = lt.id
        JOIN loan_statuses ls ON lr.status_id = ls.id
        WHERE lr.status_id = :status
        LIMIT :limit OFFSET :offset
   """)
    Flux<LoanRequestExtendedDTO> findByStatus(@Param("status") Long status,
                                              @Param("limit") int limit,
                                              @Param("offset") int offset);

    @Query("""
    SELECT
          lr.id,
          lr.document_number,
          lr.amount,
          lr.term_months,
          lt.id as loan_type_id,
          lt.name as loan_type_name,
          lt.description as loan_type_description,
          lt.minimum_amount as loan_type_minimum_amount,
          lt.maximum_amount as loan_type_maximum_amount,
          lt.interest_rate as loan_type_interest_rate,
          lt.automatic_validation as loan_type_automatic_validation,
          ls.id AS status_id,
          ls.code AS status_code,
          ls.description AS status_description,
          lr.created_at,
          COALESCE((
               SELECT SUM(
                   (approved.amount * (1 + approved_loan_type.interest_rate * approved.term_months)) / approved.term_months
               )
               FROM loan_requests approved
               JOIN loan_types approved_loan_type ON approved.loan_type_id = approved_loan_type.id
               JOIN loan_statuses approved_status ON approved.status_id = approved_status.id
               WHERE approved.document_number = lr.document_number
                 AND approved_status.code = 'APROB' 
           ), 0) AS approved_monthly_debt
        FROM loan_requests lr
        JOIN loan_types lt ON lr.loan_type_id = lt.id
        JOIN loan_statuses ls ON lr.status_id = ls.id
        WHERE lr.status_id IN (:statuses)
        LIMIT :limit OFFSET :offset
   """)
    Flux<LoanRequestExtendedDTO> findLoanRequestsByStatuses(@Param("status") List<Long> statuses,
                                                            @Param("limit") int limit,
                                                            @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM loan_requests WHERE status_id IN (:statusIds)")
    Mono<Long> countLoanRequestsByStatusIn(List<Long> statusIds);
}
