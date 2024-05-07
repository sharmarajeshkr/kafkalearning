package com.sharma.jpa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharma.entity.LibraryEvent;
import com.sharma.jpa.repository.LibraryEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LibraryEventService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LibraryEventRepository libraryEventRepository;

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord)
            throws JsonProcessingException, IllegalAccessException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("Library Event {} ", libraryEvent);

        switch (libraryEvent.getLibraryEventType()) {
            // Save
            case NEW -> {
                save(libraryEvent);
            }
            // validate and save library Event
            case UPDATE -> {
                validate(libraryEvent);
                save(libraryEvent);
            }
        }
    }

    private void validate(LibraryEvent libraryEvent) throws IllegalAccessException {
        if (libraryEvent.getLibraryEventId() == null)
            throw new IllegalAccessException("Library Event Id is missing");
        if (libraryEvent.getLibraryEventType() == null)
            throw new IllegalAccessException("Library Event Type is missing");

        Optional<LibraryEvent> libraryEventOptional =
                libraryEventRepository.findById(libraryEvent.getLibraryEventId());
        if (!libraryEventOptional.isPresent()) {
            throw new IllegalAccessException("Not a valid Library Event");
        }
        log.info("Validation is successfully for library event : {}", libraryEventOptional.get());

    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventRepository.save(libraryEvent);
        log.info("Successfully Inserted");
    }
}
