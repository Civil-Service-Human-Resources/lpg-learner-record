package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Actor;
import gov.adlnet.xapi.model.IStatementObject;
import gov.adlnet.xapi.model.Result;
import gov.adlnet.xapi.model.Statement;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.BookingDto;

@Component
public class StatementFactory {

    private final ActorFactory actorFactory;
    private final ResultFactory resultFactory;
    private final IStatementObjectFactory objectFactory;
    private final VerbFactory verbFactory;
    private final ContextFactory contextFactory;

    public StatementFactory(ActorFactory actorFactory, ResultFactory resultFactory, IStatementObjectFactory objectFactory, VerbFactory verbFactory, ContextFactory contextFactory) {
        this.actorFactory = actorFactory;
        this.resultFactory = resultFactory;
        this.objectFactory = objectFactory;
        this.verbFactory = verbFactory;
        this.contextFactory = contextFactory;
    }

    public Statement createRegisteredStatement(BookingDto bookingDto) {
        Actor actor = actorFactory.create(bookingDto.getLearner());

        IStatementObject object = objectFactory.createEventObject(bookingDto.getEvent().toString());

        Statement statement = new Statement();
        statement.setActor(actor);
        statement.setVerb(verbFactory.createdRegistered());
        statement.setObject(object);
        statement.setContext(contextFactory.createBookingContext(bookingDto));

        if (bookingDto.getPaymentDetails() != null) {
            Result result = resultFactory.createPurchaseOrderResult(bookingDto.getPaymentDetails().toString());
            statement.setResult(result);
        }

        return statement;
    }

    public Statement createApprovedStatement(BookingDto bookingDto) {
        Actor actor = actorFactory.create(bookingDto.getLearner());

        IStatementObject object = objectFactory.createEventObject(bookingDto.getEvent().toString());

        Statement statement = new Statement();
        statement.setActor(actor);
        statement.setVerb(verbFactory.createdApproved());
        statement.setObject(object);
        statement.setContext(contextFactory.createBookingContext(bookingDto));

        return statement;
    }

    public Statement createUnregisteredStatement(BookingDto bookingDto) {
        Actor actor = actorFactory.create(bookingDto.getLearner());
        IStatementObject object = objectFactory.createEventObject(bookingDto.getEvent().toString());

        Statement statement = new Statement();
        statement.setActor(actor);
        statement.setVerb(verbFactory.createdUnregistered());
        statement.setObject(object);
        statement.setContext(contextFactory.createBookingContext(bookingDto));

        return statement;
    }
}
