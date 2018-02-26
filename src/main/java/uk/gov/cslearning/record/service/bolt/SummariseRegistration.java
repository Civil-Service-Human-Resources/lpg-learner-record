package uk.gov.cslearning.record.service.bolt;

import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBatchBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import uk.gov.cslearning.record.domain.Registration;
import uk.gov.cslearning.record.service.xapi.Statement;

import java.time.LocalDateTime;
import java.util.*;

public class SummariseRegistration extends BaseBatchBolt {

    private BatchOutputCollector collector;
    private Object id;
    private Map<String, List<Statement>> groupedStatements;

    @Override
    public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
        this.collector = collector;
        this.id = id;
        this.groupedStatements = new HashMap<>();
    }

    @Override
    public void execute(Tuple tuple) {
        Statement statement = (Statement) tuple.getValue(2);
        if (!groupedStatements.containsKey(statement.getActivityId())) {
            groupedStatements.put(statement.getActivityId(), new ArrayList<>());
        }
        groupedStatements.get(statement.getActivityId()).add(statement);
    }

    @Override
    public void finishBatch() {

        for (List<Statement> statements : groupedStatements.values()) {

            statements.sort(Comparator.comparing(Statement::getTimestamp));

            LocalDateTime lastUpdated = null;
            String activityId = null;
            String userId = null;
            String state = null;

            for (Statement statement : statements) {
                if (activityId == null) {
                    activityId = statement.getActivityId();
                }
                if (userId == null) {
//                    userId = statement.get();
                }
                switch (statement.getVerb()) {
                    case REGISTERED:
                        state = "registered";
                        lastUpdated = statement.getTimestamp();
                        break;
                    case UNREGISTERED:
                        state = "unregistered";
                        lastUpdated = statement.getTimestamp();
                        break;
                }
            }

            Registration registration = null;
            if (activityId != null) {
                registration = new Registration(activityId, state, userId, lastUpdated);
            }
            collector.emit(new Values(id, registration));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "record"));
    }
}
