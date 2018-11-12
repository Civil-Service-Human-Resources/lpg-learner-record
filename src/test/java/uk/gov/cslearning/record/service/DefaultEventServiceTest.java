package uk.gov.cslearning.record.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.factory.EventFactory;
import uk.gov.cslearning.record.repository.EventRepository;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventFactory eventFactory;

    @InjectMocks
    private DefaultEventService defaultEventService;

    @Test
    public void shouldGetEvent(){
        String eventUid = "test-id";
        String path = "/test/test-id";

        Event event = new Event();

        Mockito.when(eventRepository.findByEventUid(eventUid)).thenReturn(Optional.of(event));

        Assert.assertEquals(defaultEventService.getEvent(eventUid, path), event);
    }

    @Test
    public void shouldCreateEventIfNotPresent(){
        String eventUid = "test-id";
        String path = "/test/test-id";

        Event event = new Event();

        Mockito.when(eventRepository.findByEventUid(eventUid)).thenReturn(Optional.empty()).thenReturn(Optional.of(event));
        Mockito.when(eventFactory.create(path)).thenReturn(event);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        Assert.assertEquals(defaultEventService.getEvent(eventUid, path), event);
        Mockito.verify(eventRepository).save(event);
    }
}
