package uk.gov.cslearning.record.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.storm.Config;
import org.apache.storm.ILocalDRPC;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.LinearDRPCTopologyBuilder;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.DistributedRPC;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.thrift.TException;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.bolt.GetStatementsForActivity;
import uk.gov.cslearning.record.service.bolt.RecordAggregator;
import uk.gov.cslearning.record.service.bolt.SummariseRecord;
import uk.gov.cslearning.record.service.xapi.XApiService;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class ActivityRecordService {

    private static final String FUNCTION = "activity-record";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityRecordService.class);

    private DistributedRPC.Iface client;

    @Autowired(required = false)
    private LocalCluster cluster;

    private XApiService xApiService;

    @Autowired
    public ActivityRecordService(DistributedRPC.Iface client, XApiService xApiService) {
        checkArgument(client != null);
        checkArgument(xApiService != null);
        this.client = client;
        this.xApiService = xApiService;
    }

    @PostConstruct
    public void configure() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        LOGGER.debug("Configuring activity record topology");

        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder(FUNCTION);
        builder.addBolt(new GetStatementsForActivity(xApiService));
        builder.addBolt(new SummariseRecord())
                .fieldsGrouping(new Fields("id", "userId"));
        builder.addBolt(new RecordAggregator())
                .fieldsGrouping(new Fields("id"));

        Config config = new Config();

        if (cluster != null) {
            cluster.submitTopology(FUNCTION, config, builder.createLocalTopology((ILocalDRPC) client));
        } else {
            config.putAll(Utils.readDefaultConfig());
            // TODO version or timestamp function
            StormSubmitter.submitTopology(FUNCTION, config, builder.createRemoteTopology());
        }
    }

    public List<Record> getActivityRecord(String activityId) {
        LOGGER.debug("Retrieving activity record");
        try {
            Gson gson = new Gson();
            String response = client.execute(FUNCTION, activityId);
            return Lists.newArrayList(gson.fromJson(response, Record[].class));
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }
}
