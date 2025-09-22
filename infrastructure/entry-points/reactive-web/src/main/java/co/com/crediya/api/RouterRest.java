package co.com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> solicitudRoutes(Handler handler) {
        return route()
                .POST("/api/v1/solicitud", handler::loanRequest)
                .GET("/api/v1/solicitud", handler::getLoanRequestsByType)
                .PATCH("/api/v1/solicitud/{id}/estado", handler::updateLoanStatus)
                .build();
    }
}
