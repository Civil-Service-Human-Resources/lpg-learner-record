package uk.gov.cslearning.record.config;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;
import org.apache.storm.utils.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;

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
            config.putAll(Utils.readDefaultConfig());
            return new DRPCClient(config, properties.getHost(), properties.getPort());
        }

        @EventListener(ContextStartedEvent.class)
        public void contextStarted() {
            if (System.getProperty("storm.jar") == null) {
                System.setProperty("storm.jar", "build/libs/learner-record.jar");
            }
        }
    }
}
