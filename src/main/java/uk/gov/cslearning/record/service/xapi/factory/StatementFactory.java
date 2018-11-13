package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.*;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.BookingDto;

@Component
public class StatementFactory {

    private final ActorFactory actorFactory;
    private final ResultFactory resultFactory;
    private final IStatementObjectFactory objectFactory;
    private final VerbFactory verbFactory;

    public StatementFactory(ActorFactory actorFactory, ResultFactory resultFactory, IStatementObjectFactory objectFactory, VerbFactory verbFactory) {
        this.actorFactory = actorFactory;
        this.resultFactory = resultFactory;
        this.objectFactory = objectFactory;
        this.verbFactory = verbFactory;
    }

    public Statement createRegisteredStatement(BookingDto bookingDto) {
        Actor actor = actorFactory.create(bookingDto.getLearner());

        Result result = resultFactory.createPurchaseOrderResult(bookingDto.getPaymentDetails().toString());

        IStatementObject object = objectFactory.createEventObject(bookingDto.getEvent().toString());

        Statement statement = new Statement();
        statement.setActor(actor);
        statement.setVerb(verbFactory.createdRegistered());
        statement.setObject(object);
        statement.setResult(result);

        return statement;
    }

    public Statement createUnregisteredStatement(BookingDto bookingDto) {
        Actor actor = actorFactory.create(bookingDto.getLearner());
        IStatementObject object = objectFactory.createEventObject(bookingDto.getEvent().toString());

        Statement statement = new Statement();
        statement.setActor(actor);
        statement.setVerb(verbFactory.createdUnregistered());
        statement.setObject(object);

        return statement;
    }
}
