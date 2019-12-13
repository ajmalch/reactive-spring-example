package com.example.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.time.Instant;

@Slf4j
@Component
@Order(-2)
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {


    public ExceptionHandler(DefaultErrorAttributes defaultErrorAttributes, ApplicationContext applicationContext,
                            ServerCodecConfigurer serverCodecConfigurer) {
        super(defaultErrorAttributes, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }


    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Throwable error = getError(request);
        MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .get(ResponseStatus.class);
        final var status = error instanceof ConstraintViolationException ? HttpStatus.BAD_REQUEST : determineHttpStatus(error, responseStatusAnnotation);
        return ServerResponse.status(
                status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sendResponse(status, getError(request))), ExceptionResponseMessage.class);
    }

    private ExceptionResponseMessage sendResponse(HttpStatus status, Throwable ex) {

        return new ExceptionResponseMessage(Instant.now(), status.value(), status.getReasonPhrase(),
                ex.getClass()
                        .toString(), ex.getMessage());
    }

    private HttpStatus determineHttpStatus(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
        return error instanceof ResponseStatusException ? ((ResponseStatusException) error).getStatus() : responseStatusAnnotation.getValue("code", HttpStatus.class)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@AllArgsConstructor
@Data
class ExceptionResponseMessage {

    private Instant time;
    private int status;
    private String error;
    private String exception;
    private String message;

}


