package uk.gov.cslearning.record.service.bolt;

import com.google.gson.Gson;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.service.LearnerRecordService;
import uk.gov.cslearning.record.service.LearnerRecordService.Arguments;
import uk.gov.cslearning.record.service.xapi.Statement;
import uk.gov.cslearning.record.service.xapi.XApiService;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public class GetStatements extends BaseBasicBolt {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetStatements.class);

    private XApiService xApiService;

    public GetStatements(XApiService xApiService) {
        checkArgument(xApiService != null);
        this.xApiService = xApiService;
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        LOGGER.debug("input {}", input);

        String encodedArguments = input.getString(1);
        Gson gson = new Gson();

        Arguments arguments = gson.fromJson(encodedArguments, Arguments.class);

        Collection<Statement> statements = xApiService.getStatements(arguments.userId, arguments.activityId);

        for (Statement statement : statements) {
            collector.emit(new Values(input.getValue(0), statement.getActivityId(), statement));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "activityId", "statement"));
    }
}
