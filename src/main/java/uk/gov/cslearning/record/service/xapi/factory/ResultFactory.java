package uk.gov.cslearning.record.service.xapi.factory;

import com.google.gson.JsonObject;
import gov.adlnet.xapi.model.Result;
import org.springframework.stereotype.Component;

@Component
public class ResultFactory {

    public Result createPurchaseOrderResult(String paymentDetails) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("http://cslearning.gov.uk/extension/payment", paymentDetails);

        Result result = new Result();
        result.setExtensions(jsonObject);

        return result;
    }
}
