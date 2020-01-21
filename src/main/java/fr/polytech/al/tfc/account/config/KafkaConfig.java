package fr.polytech.al.tfc.account.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfig {
    private static final String app = "kafka-account";
    @Bean
    public KafkaConsumer<String, String> consumer(@Value("${kafkabroker}") String kafkaBrokers){

        String groupId = String.format("%s", app);

        Map<String, Object> config = new HashMap<>();
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        StringDeserializer deserializer = new StringDeserializer();

        return new KafkaConsumer<>(config, deserializer, deserializer);
    }
    @Bean
    public KafkaProducer<String,String> producer(@Value("${kafkabroker}") String kafkaBrokers){
        Map<String, Object> config = new HashMap<>();
        config.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);

        StringSerializer deserializer = new StringSerializer();

        return new KafkaProducer<>(config, deserializer, deserializer);
    }
}
