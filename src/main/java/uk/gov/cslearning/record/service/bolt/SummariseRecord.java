package uk.gov.cslearning.record.service.bolt;

import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBatchBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.xapi.Statement;

import java.time.LocalDateTime;
import java.util.*;

public class SummariseRecord extends BaseBatchBolt {

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

            LocalDateTime completionDate = null;
            String activityId = null;
            String state = null;
            String result = null;
            // TODO: score
            String score = null;
            String preference = null;

            for (Statement statement : statements) {
                if (activityId == null) {
                    activityId = statement.getActivityId();
                }
                switch (statement.getVerb()) {
                    case LIKED:
                        preference = "liked";
                        break;
                    case DISLIKED:
                        preference = "disliked";
                        break;
                    case FAILED:
                        result = "failed";
                        break;
                    case PASSED:
                        result = "passed";
                        break;
                    case COMPLETED:
                        state = "completed";
                        completionDate = statement.getTimestamp();
                        break;
                    case LAUNCHED:
                    case INITIALISED:
                        state = "in-progress";
                        score = null;
                        result = null;
                        completionDate = null;
                        break;
                    case TERMINATED:
                        state = "terminated";
                        score = null;
                        result = null;
                        completionDate = null;
                        break;
                    case REGISTERED:
                        state = "registered";
                        score = null;
                        result = null;
                        completionDate = null;
                        break;
                    case UNREGISTERED:
                        state = "unregistered";
                        score = null;
                        result = null;
                        completionDate = null;
                        break;
                }
            }

            Record record = null;
            if (activityId != null) {
                record = new Record(activityId, state, result,preference, score, completionDate);
            }
            collector.emit(new Values(id, record));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "record"));
    }
}
