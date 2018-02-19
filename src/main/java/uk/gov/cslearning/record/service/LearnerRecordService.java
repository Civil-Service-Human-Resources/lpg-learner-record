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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.bolt.GetStatements;
import uk.gov.cslearning.record.service.bolt.RecordAggregator;
import uk.gov.cslearning.record.service.bolt.SummariseRecord;
import uk.gov.cslearning.record.service.xapi.XApiService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class LearnerRecordService {

    private static final String FUNCTION = "learner-record";

    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerRecordService.class);

    private DistributedRPC.Iface client;

    private LocalCluster cluster;

    private XApiService xApiService;

    @Autowired
    public LearnerRecordService(DistributedRPC.Iface client, XApiService xApiService, LocalCluster cluster) {
        checkArgument(client != null);
        checkArgument(xApiService != null);
        this.client = client;
        this.xApiService = xApiService;
        this.cluster = cluster;
    }

    @PostConstruct
    public void configure() throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        LOGGER.debug("Configuring learner record topology");

        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder(FUNCTION);
        builder.addBolt(new GetStatements(xApiService));
        builder.addBolt(new SummariseRecord())
            .fieldsGrouping(new Fields("id", "activityId"));
        builder.addBolt(new RecordAggregator())
            .fieldsGrouping(new Fields("id"));

        if (cluster != null) {
            cluster.submitTopology(FUNCTION, new Config(), builder.createLocalTopology((ILocalDRPC) client));
        } else {
            StormSubmitter.submitTopology(FUNCTION, new Config(), builder.createRemoteTopology());
        }
    }

    public List<Record> getLearnerRecord(String userId, String activityId) {
        LOGGER.debug("Retrieving learner record for user {}, activity {} and state {}", userId, activityId);
        try {
            Gson gson = new Gson();
            String response = client.execute(FUNCTION, gson.toJson(new Arguments(userId, activityId)));
            return Lists.newArrayList(gson.fromJson(response, Record[].class));
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Arguments implements Serializable {

        public String userId;
        public String activityId;

        public Arguments(String userId, String activityId) {
            this.userId = userId;
            this.activityId = activityId;
        }
    }
}
