package uk.gov.cslearning.record.service.bolt;

import com.google.gson.Gson;
import org.apache.storm.coordination.BatchOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBatchBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import uk.gov.cslearning.record.domain.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistrationAggregator extends BaseBatchBolt {

    private BatchOutputCollector collector;
    private Object id;
    private List<Registration> registrations;

    @Override
    public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
        this.collector = collector;
        this.id = id;
        this.registrations = new ArrayList<>();
    }

    @Override
    public void execute(Tuple tuple) {
        Registration registration = (Registration) tuple.getValue(1);
        if (registration != null) {
            registrations.add(registration);
        }
    }

    @Override
    public void finishBatch() {
        Gson gson = new Gson();
        collector.emit(new Values(id, gson.toJson(registrations)));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "registrations"));
    }
}
