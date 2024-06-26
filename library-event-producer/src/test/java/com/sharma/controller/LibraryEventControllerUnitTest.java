package com.sharma.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharma.TestUtil;
import com.sharma.domain.LibraryEvent;
import com.sharma.producer.LibraryEventsProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {
        // given
        var libraryEvent = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());
        when(libraryEventsProducer.sendLibraryEventSynchronous(isA(LibraryEvent.class)))
                .thenReturn(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                .content(libraryEvent)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());

        // then

    }


    @Test
    void postLibraryEvent_4xx() throws Exception {
        var actualErrorMessage = "book.bookId - must not be null, book.bookName - must not be blank";
        // given
        var libraryEvent = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());
        when(libraryEventsProducer.sendLibraryEventSynchronous(isA(LibraryEvent.class)))
                .thenReturn(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(libraryEvent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(actualErrorMessage));

        // then

    }


    @Test
    void updateLibraryEvent() throws Exception {
        // given
        var libraryEvent = objectMapper.writeValueAsString(TestUtil.libraryEventRecordUpdate());
        when(libraryEventsProducer.sendLibraryEventSynchronous(isA(LibraryEvent.class)))
                .thenReturn(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                        .content(libraryEvent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @DisplayName("Update Library event when event-id is null")
    @Test
    void updateLibraryEvent_EventId_null() throws Exception {
        // given
        var libraryEvent = objectMapper.writeValueAsString(TestUtil.libraryEventRecordUpdateWithNullLibraryEventId());
        when(libraryEventsProducer.sendLibraryEventSynchronous(isA(LibraryEvent.class)))
                .thenReturn(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                        .content(libraryEvent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("Please provide valid Library Event Id"));
    }

    @DisplayName("Update Library event when event-type is NEW")
    @Test
    void updateLibraryEvent_EventType_New() throws Exception {
        // given
        var libraryEvent = objectMapper.writeValueAsString(TestUtil.newLibraryEventRecordWithLibraryEventId());
        when(libraryEventsProducer.sendLibraryEventSynchronous(isA(LibraryEvent.class)))
                .thenReturn(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                        .content(libraryEvent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("Only Update Event is supported"));
    }
}
