package uk.gov.cslearning.record.service.xapi;

import gov.adlnet.xapi.client.StatementClient;
import gov.adlnet.xapi.model.Statement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.config.XApiProperties;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.service.xapi.exception.XApiException;
import uk.gov.cslearning.record.service.xapi.factory.StatementClientFactory;
import uk.gov.cslearning.record.service.xapi.factory.StatementFactory;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class XApiServiceTest {

    @Mock
    private StatementFactory statementFactory;

    @Mock
    private StatementClientFactory statementClientFactory;

    @Mock
    private XApiProperties xApiProperties;

    @InjectMocks
    private XApiService xApiService;

    @Test
    public void shouldRegisterEvent() throws IOException {
        String recordId = "record-id";
        BookingDto bookingDto = new BookingDto();
        Statement statement = new Statement();
        StatementClient statementClient = mock(StatementClient.class);

        when(statementClientFactory.create()).thenReturn(statementClient);
        when(statementFactory.createRegisteredStatement(bookingDto)).thenReturn(statement);
        when(statementClient.postStatement(statement)).thenReturn(recordId);

        assertEquals(recordId, xApiService.register(bookingDto));

        verify(statementClient).postStatement(statement);
    }

    @Test
    public void shouldThrowXApiException() throws IOException {
        BookingDto bookingDto = new BookingDto();
        Statement statement = new Statement();
        StatementClient statementClient = mock(StatementClient.class);

        when(statementClientFactory.create()).thenReturn(statementClient);
        when(statementFactory.createRegisteredStatement(bookingDto)).thenReturn(statement);
        IOException exception = mock(IOException.class);
        doThrow(exception).when(statementClient).postStatement(statement);

        try {
            xApiService.register(bookingDto);
            fail("Expected XApiException");
        } catch (XApiException e) {
            assertEquals("Unable to post statement to XApi", e.getMessage());
            assertEquals(exception, e.getCause());
        }
    }
}