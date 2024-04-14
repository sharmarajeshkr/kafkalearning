package com.sharma.controller;

import com.sharma.TestUtil;
import com.sharma.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// Use Embedded Kafka instead of local kafka
@EmbeddedKafka(topics = "library-events")
// Override kafka producer bootstrap address to embedded brokers
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})

// Disabling @EmbeddedKafka and @TestPropertySource required local kafka setup to complete this test case
class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void postLibraryEvent() {
        //given
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("content-type", MediaType.APPLICATION_JSON.toString());
        var httpEntity = new HttpEntity<>(TestUtil.libraryEventRecord(), httpHeaders);

        //when
        var responseEntity = restTemplate
                .exchange("/v1/libraryevent", HttpMethod.POST,
                        httpEntity, LibraryEvent.class);


        //then
        assertEquals( HttpStatus.CREATED, responseEntity.getStatusCode());

    }
}