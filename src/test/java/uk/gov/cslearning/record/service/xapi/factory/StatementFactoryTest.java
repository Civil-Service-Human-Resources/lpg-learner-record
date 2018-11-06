package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.dto.BookingDto;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatementFactoryTest {

    @Mock
    private ActorFactory actorFactory;

    @Mock
    private IStatementObjectFactory objectFactory;

    @Mock
    private ResultFactory resultFactory;

    @Mock
    private VerbFactory verbFactory;

    @InjectMocks
    private StatementFactory statementFactory;

    @Test
    public void shouldReturnStatement() throws URISyntaxException {
        String userId = "learner-uuid";
        URI paymentDetails = new URI("http://csrs/payment-details");
        URI event = new URI("http://learning-catalogue/event-details");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setLearner(userId);
        bookingDto.setPaymentDetails(paymentDetails);
        bookingDto.setEvent(event);

        Actor actor = mock(Actor.class);
        IStatementObject object = mock(IStatementObject.class);
        Result result = new Result();
        Verb registered = new Verb();

        when(actorFactory.create(userId)).thenReturn(actor);
        when(objectFactory.createEventObject(event.toString())).thenReturn(object);
        when(resultFactory.createPurchaseOrderResult(paymentDetails.toString())).thenReturn(result);
        when(verbFactory.createdRegistered()).thenReturn(registered);

        Statement statement = statementFactory.createRegisteredStatement(bookingDto);

        assertEquals(actor, statement.getActor());
        assertEquals(object, statement.getObject());
        assertEquals(result, statement.getResult());
        assertEquals(registered, statement.getVerb());

        verify(actorFactory).create(userId);
        verify(objectFactory).createEventObject(event.toString());
        verify(resultFactory).createPurchaseOrderResult(paymentDetails.toString());
        verify(verbFactory).createdRegistered();
    }
}