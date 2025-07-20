package com.example.cqrs.application;

import an.awesome.pipelinr.Pipeline;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.channels.Pipe;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = com.example.cqrs.CqrsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContextLoadTest {

    @Autowired
    private Pipeline pipeline;

    @Test
    void contextLoads() {
        assertThat(pipeline).isNotNull();
    }
}
