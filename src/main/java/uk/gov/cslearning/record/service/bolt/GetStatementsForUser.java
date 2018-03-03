package uk.gov.cslearning.record.service.bolt;

import com.google.gson.Gson;
import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Statement;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.service.LearnerRecordService.Arguments;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public class GetStatementsForUser extends BaseBasicBolt {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetStatementsForUser.class);

    private XApiService xApiService;

    public GetStatementsForUser(XApiService xApiService) {
        checkArgument(xApiService != null);
        this.xApiService = xApiService;
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        LOGGER.debug("input {}", input);

        String encodedArguments = input.getString(1);
        Gson gson = new Gson();

        Arguments arguments = gson.fromJson(encodedArguments, Arguments.class);

        try {
            Collection<Statement> statements = xApiService.getStatements(arguments.userId, arguments.activityId);
            for (Statement statement : statements) {
                if (!(statement.getObject() instanceof Activity)) {
                    continue;
                }
                String activityId = ((Activity) statement.getObject()).getId();
                collector.emit(new Values(input.getValue(0), activityId, statement));
            }
        } catch (IOException e) {
            LOGGER.error("Exception retrieving xAPI statements.", e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "activityId", "statement"));
    }
}
