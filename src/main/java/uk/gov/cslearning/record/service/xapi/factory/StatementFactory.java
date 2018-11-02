package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.*;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.BookingDto;

@Component
public class StatementFactory {

    private final ActorFactory actorFactory;
    private final ResultFactory resultFactory;
    private final IStatementObjectFactory objectFactory;

    public StatementFactory(ActorFactory actorFactory, ResultFactory resultFactory, IStatementObjectFactory objectFactory) {
        this.actorFactory = actorFactory;
        this.resultFactory = resultFactory;
        this.objectFactory = objectFactory;
    }

    public Statement createRegisteredStatement(BookingDto bookingDto) {
        Actor actor = actorFactory.create(bookingDto.getLearner());

        Result result = resultFactory.createPurchaseOrderResult(bookingDto.getPaymentDetails());

        IStatementObject object = objectFactory.createEventObject(bookingDto.getEvent());

        Statement statement = new Statement();
        statement.setActor(actor);
        statement.setVerb(Verbs.registered());
        statement.setObject(object);
        statement.setResult(result);

        return statement;
    }
}
