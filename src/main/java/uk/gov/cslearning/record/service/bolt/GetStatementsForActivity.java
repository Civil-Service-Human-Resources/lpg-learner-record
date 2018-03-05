package uk.gov.cslearning.record.service.bolt;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Agent;
import gov.adlnet.xapi.model.Statement;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public class GetStatementsForActivity extends BaseBasicBolt {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetStatementsForActivity.class);

    private XApiService xApiService;

    public GetStatementsForActivity(XApiService xApiService) {
        checkArgument(xApiService != null);
        this.xApiService = xApiService;
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        LOGGER.debug("input {}", input);

        String activityId = input.getString(1);

        try {
            Collection<Statement> statements = xApiService.getStatements(activityId);
            for (Statement statement : statements) {
                if (!(statement.getObject() instanceof Activity)) {
                    continue;
                }
                String userId = statement.getActor().getName();
                collector.emit(new Values(input.getValue(0), userId, statement));
            }
        } catch (IOException e) {
            LOGGER.error("Exception retrieving xAPI statements.", e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "userId", "statement"));
    }
}
