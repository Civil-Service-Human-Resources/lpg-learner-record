package uk.gov.cslearning.record.service.bolt;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.cslearning.record.service.xapi.Statement;
import uk.gov.cslearning.record.service.xapi.Verb;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public class GetRegistrationStatements extends BaseBasicBolt {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetRegistrationStatements.class);

    private XApiService xApiService;

    public GetRegistrationStatements(XApiService xApiService) {
        checkArgument(xApiService != null);
        this.xApiService = xApiService;
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        LOGGER.debug("input {}", input);

        Collection<Statement> statements = xApiService.getStatements(Verb.REGISTERED);
        statements.addAll(xApiService.getStatements(Verb.UNREGISTERED));

        for (Statement statement : statements) {
            collector.emit(new Values(input.getValue(0), statement.getActivityId(), statement));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "activityId", "statement"));
    }
}
