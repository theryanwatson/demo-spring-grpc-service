package org.watson.demos.controllers;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.BeanUtils;
import org.watson.demos.GreetingControllerGrpc;
import org.watson.demos.GreetingFilter;
import org.watson.demos.GreetingRequest;
import org.watson.demos.GreetingResponse;
import org.watson.demos.Identifiable;
import org.watson.demos.models.Greeting;
import org.watson.demos.models.GreetingProbe;
import org.watson.demos.services.GreetingService;

import javax.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@GrpcService
public class GreetingController extends GreetingControllerGrpc.GreetingControllerImplBase {
    private final GreetingService service;

    @Override
    public void get(final Identifiable request, final StreamObserver<GreetingResponse> observer) {
        request.getIdList().forEach(id -> Optional.of(id)
                .map(Converter::toUuid)
                .flatMap(service::getOne)
                .map(Converter::toGreetingResponse)
                .ifPresentOrElse(observer::onNext, () -> observer.onError(new EntityNotFoundException(id + " not found"))));

        observer.onCompleted();
    }

    @Override
    public void getAll(final GreetingFilter request, final StreamObserver<GreetingResponse> observer) {
        Optional.of(request)
                .map(Converter::toGreetingProbe)
                .map(service::getAll)
                .map(Iterable::spliterator)
                .map(s -> StreamSupport.stream(s, false))
                .orElseGet(Stream::empty)
                .map(Converter::toGreetingResponse)
                .forEach(observer::onNext);

        observer.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> create(final StreamObserver<GreetingResponse> observer) {
        return new StreamObserver<>() {
            @Override
            public void onNext(final GreetingRequest request) {
                Optional.of(request)
                        .map(Converter::toGreeting)
                        .map(service::create)
                        .map(Converter::toGreetingResponse)
                        .ifPresentOrElse(observer::onNext, () -> observer.onError(new Exception("Unable to create")));
            }

            @Override
            public void onError(final Throwable throwable) {
                observer.onError(throwable);
            }

            @Override
            public void onCompleted() {
                observer.onCompleted();
            }
        };
    }

    @Override
    public void delete(final Identifiable request, final StreamObserver<Empty> observer) {
        request.getIdList().stream()
                .map(Converter::toUuid)
                .peek(service::delete)
                .forEach(ignored -> observer.onNext(Empty.newBuilder().build()));

        observer.onCompleted();
    }

    @UtilityClass
    private static class Converter {
        static GreetingResponse toGreetingResponse(final Greeting original) {
            return original == null ? null : GreetingResponse.newBuilder()
                    .setContent(original.getContent())
                    .setCreated(toTimestamp(original.getCreated()))
                    .setLocale(toString(original.getLocale()))
                    .setId(toString(original.getId()))
                    .build();
        }

        static Greeting toGreeting(final GreetingRequest original) {
            return original == null ? null : Greeting.builder()
                    .content(original.getContent())
                    .locale(Locale.forLanguageTag(original.getLocale()))
                    .build();
        }

        static GreetingProbe toGreetingProbe(final GreetingFilter original) {
            return original == null ? null : GreetingProbe.builder()
                    .locale(original.hasLocale() ? Locale.forLanguageTag(original.getLocale()) : null)
                    .build();
        }

        static UUID toUuid(final String original) {
            return original == null ? null : UUID.fromString(original);
        }

        static String toString(final Object original) {
            return original == null ? null : original.toString();
        }

        static Timestamp toTimestamp(final ZonedDateTime original) {
            return Optional.ofNullable(original)
                    .map(ZonedDateTime::toInstant)
                    .map(i -> Timestamp.newBuilder()
                            .setSeconds(i.getEpochSecond())
                            .setNanos(i.getNano()))
                    .map(Timestamp.Builder::build)
                    .orElse(null);
        }
    }
}
