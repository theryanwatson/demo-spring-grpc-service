package org.watson.demos.controllers;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.watson.demos.GreetingControllerGrpc;

@SpringBootTest(classes = GreetingController.class, properties = {
        "grpc.server.inProcessName=test", // Enable inProcess server
        "grpc.server.port=-1", // Disable external server
        "grpc.client.inProcess.address=in-process:test", // Configure the client to connect to the inProcess server
})
class GreetingControllerIntegrationTest {
    @GrpcClient("inProcess")
    private GreetingControllerGrpc.GreetingControllerStub client;

    @Test
    void test() {
        // TODO
    }
}
