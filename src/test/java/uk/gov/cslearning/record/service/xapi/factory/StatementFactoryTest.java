package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Actor;
import gov.adlnet.xapi.model.IStatementObject;
import gov.adlnet.xapi.model.Result;
import gov.adlnet.xapi.model.Statement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.dto.BookingDto;

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

    @InjectMocks
    private StatementFactory statementFactory;

    @Test
    public void shouldReturnStatement() {
        String userId = "learner-uuid";
        String paymentDetails = "payment-details";
        String eventId = "event-details";

        BookingDto bookingDto = new BookingDto();
        bookingDto.setLearner(userId);
        bookingDto.setPaymentDetails(paymentDetails);
        bookingDto.setEvent(eventId);

        Actor actor = mock(Actor.class);
        IStatementObject object = mock(IStatementObject.class);
        Result result = new Result();

        when(actorFactory.create(userId)).thenReturn(actor);
        when(objectFactory.createEventObject(eventId)).thenReturn(object);
        when(resultFactory.createPurchaseOrderResult(paymentDetails)).thenReturn(result);

        Statement statement = statementFactory.createRegisteredStatement(bookingDto);

        assertEquals(actor, statement.getActor());
        assertEquals(object, statement.getObject());
        assertEquals(result, statement.getResult());

        verify(actorFactory).create(userId);
        verify(objectFactory).createEventObject(eventId);
        verify(resultFactory).createPurchaseOrderResult(paymentDetails);
    }
}