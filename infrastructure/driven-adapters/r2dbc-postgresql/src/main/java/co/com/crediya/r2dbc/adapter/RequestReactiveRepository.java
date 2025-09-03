package co.com.crediya.r2dbc.adapter;

import co.com.crediya.r2dbc.dto.LoanRequestExtendedDTO;
import co.com.crediya.r2dbc.entity.LoanRequestEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;




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
    Flux<LoanRequestExtendedDTO> findByStatus(@Param("status") Integer status,
                                              @Param("limit") int limit,
                                              @Param("offset") int offset);
}
