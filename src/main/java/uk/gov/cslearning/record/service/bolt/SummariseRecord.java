package uk.gov.cslearning.record.service.bolt;

import gov.adlnet.xapi.model.Statement;
import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBatchBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.xapi.StatementStream;
import uk.gov.cslearning.record.service.xapi.activity.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        String groupId = (String) tuple.getValue(1);
        Statement statement = (Statement) tuple.getValue(2);
        if (!groupedStatements.containsKey(groupId)) {
            groupedStatements.put(groupId, new ArrayList<>());
        }
        groupedStatements.get(groupId).add(statement);
    }

    @Override
    public void finishBatch() {

        for (Entry<String, List<Statement>> entry : groupedStatements.entrySet()) {

            List<Statement> statements = entry.getValue();

            Activity activity = Activity.getFor(statements.get(0));

            if (activity != null) {
                Record record = new Record();
                //TODO update to course / module / event
                record.setCourseId(activity.getActivityId());
                record.setUserId(statements.get(0).getActor().getAccount().getName());

                StatementStream stream = new StatementStream(statements);
                record = stream.replay(record);

                collector.emit(new Values(id, record));
            } else {
                collector.emit(new Values(id, null));
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "record"));
    }
}
