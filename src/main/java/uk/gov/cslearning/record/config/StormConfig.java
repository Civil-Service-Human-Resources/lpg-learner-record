package uk.gov.cslearning.record.config;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StormConfig {

    @Configuration
    @Profile({ "default" })
    public static class Local {

        @Bean
        public LocalDRPC client() {
            return new LocalDRPC();
        }

        @Bean
        public LocalCluster cluster() {
            return new LocalCluster();
        }
    }

    @Configuration
    @Profile({ "production" })
    public static class Remote {

        @Bean
        public DRPCClient client(StormProperties properties) throws TTransportException {
            Config config = new Config();
            return new DRPCClient(config, properties.getHost(), properties.getPort());
        }
    }
}
