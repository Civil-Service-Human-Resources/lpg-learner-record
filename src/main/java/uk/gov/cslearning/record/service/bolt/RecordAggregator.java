package uk.gov.cslearning.record.service.bolt;

import com.google.gson.Gson;
import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBatchBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import uk.gov.cslearning.record.domain.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordAggregator extends BaseBatchBolt {

    private BatchOutputCollector collector;
    private Object id;
    private List<Record> records;

    @Override
    public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
        this.collector = collector;
        this.id = id;
        this.records = new ArrayList<>();
    }

    @Override
    public void execute(Tuple tuple) {
        Record record = (Record) tuple.getValue(1);
        if (record != null) {
            records.add(record);
        }
    }

    @Override
    public void finishBatch() {
        Gson gson = new Gson();
        collector.emit(new Values(id, gson.toJson(records)));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "records"));
    }
}
