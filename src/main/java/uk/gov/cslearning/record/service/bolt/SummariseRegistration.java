package uk.gov.cslearning.record.service.bolt;

import gov.adlnet.xapi.model.Statement;
import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBatchBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String activityId = (String) tuple.getValue(1);
        Statement statement = (Statement) tuple.getValue(2);
        if (!groupedStatements.containsKey(activityId)) {
            groupedStatements.put(activityId, new ArrayList<>());
        }
        groupedStatements.get(activityId).add(statement);
    }

    @Override
    public void finishBatch() {

        for (List<Statement> statements : groupedStatements.values()) {

//            collector.emit(new Values(id, registration));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "record"));
    }
}
