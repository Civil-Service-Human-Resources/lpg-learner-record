package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Result;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResultFactoryTest {

    private ResultFactory resultFactory = new ResultFactory();

    @Test
    public void shouldReturnStatementResult() {

        String paymentDetails = "payment-details";

        Result result = resultFactory.createPurchaseOrderResult(paymentDetails);

        assertEquals(paymentDetails,
                result.getExtensions().get("http://cslearning.gov.uk/extension/payment").getAsString());
    }
}