package com.sharma.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sharma.domain.LibraryEvent;
import com.sharma.domain.LibraryEventType;
import com.sharma.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventsController {


    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventsController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }


    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("libraryEvent : {} ", libraryEvent);


        /*if (LibraryEventType.NEW != libraryEvent.libraryEventType()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only NEW event type is supported");
        }*/
        //invoke the kafka producer
        // libraryEventsProducer.sendLibraryEvent(libraryEvent);
        libraryEventsProducer.sendLibraryEvent_Approach2(libraryEvent);
        //libraryEventProducer.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
