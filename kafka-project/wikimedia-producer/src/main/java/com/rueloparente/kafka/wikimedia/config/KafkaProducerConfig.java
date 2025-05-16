package com.rueloparente.kafka.wikimedia.config;

import com.rueloparente.kafka.wikimedia.properties.WikimediaProducerProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import com.rueloparente.kafkaskeleton.proto.RecentChange;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Bean
    public Map<String, Object> wikimediaProducerConfigs(WikimediaProducerProperties wikimediaProducerProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, wikimediaProducerProperties.bootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, wikimediaProducerProperties.keySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, wikimediaProducerProperties.valueSerializer());
        Optional.ofNullable(wikimediaProducerProperties.saslProperties())
                .ifPresent(saslProperties -> {
                    props.put(SaslConfigs.SASL_MECHANISM, saslProperties.saslMechanism());
                    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, saslProperties.securityProtocol());
                    props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, saslProperties.sslEndpointIdentificationAlgorithm());
                    props.put(SaslConfigs.SASL_JAAS_CONFIG, saslProperties.saslJaasConfig());
                });

        // These are "safe producer settings", they are the default in kafka > 3 but should be implemented manually in
        // older versions
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // zero - potential data loss, 1 = aw for leader ack, limited
        // data loss, all/-1 = aw for leader and replicas ack, no data loss (current default)
        // This configuration should take into account the min.insync.replicas setting of the broker
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE)); // default zero for kafka
        // before 2.0, defaults to MAX_INT after kafka 2.1. Should consider retry.backoff.ms
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // Prevents duplicated messages

        // These are "high throughput" producer settings
        props.put(ProducerConfig.LINGER_MS_CONFIG, "20");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        return props;
    }

    @Bean
    public ProducerFactory<String, RecentChange> wikimediaProducerFactory(
            WikimediaProducerProperties wikimediaProducerProperties) {
        return new DefaultKafkaProducerFactory<>(wikimediaProducerConfigs(wikimediaProducerProperties));
    }

    @Bean
    public KafkaTemplate<String, RecentChange> wikimediaKafkaTemplate(
            WikimediaProducerProperties kafkaProperties) {
        return new KafkaTemplate<>(wikimediaProducerFactory(kafkaProperties));
    }

}
