package org.watson.demos.controllers;

import io.grpc.internal.testing.StreamRecorder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.watson.demos.GreetingRequest;
import org.watson.demos.GreetingResponse;
import org.watson.demos.Identifiable;
import org.watson.demos.services.GreetingService;
import org.watson.demos.utilities.GeneratorTestUtility;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = GreetingController.class)
class GreetingControllerTest {
    @MockBean
    private GreetingService service;
    @Resource
    private GreetingController controller;

    @SneakyThrows
    @Test
    void test() {
        List<GreetingRequest> requests = GeneratorTestUtility.generateGreetingRequests("hghgh");

        final StreamRecorder<GreetingResponse> observer = StreamRecorder.create();
        final UUID expectedId = UUID.randomUUID();
        final Identifiable request = Identifiable.newBuilder()
                .addId(expectedId.toString())
                .build();

        controller.get(request, observer);

        observer.awaitCompletion();

        verify(service).getOne(expectedId);
    }
}
