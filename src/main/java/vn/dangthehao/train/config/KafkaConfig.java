package vn.dangthehao.train.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
  @Bean
  public NewTopic userActivityTopic() {
    return TopicBuilder.name("user-activity-topic")
        .partitions(2)
        .replicas(1)
        .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(5L * 24 * 60 * 60 * 1000))
        .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "1")
        .build();
  }

  @Bean
  public NewTopic globalErrorTopic() {
    return TopicBuilder.name("global-error-topic").partitions(1).replicas(1).build();
  }

  @Bean
  public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaTemplate, (event, exception) -> new TopicPartition("global-error-topic", -1));

    FixedBackOff fixedBackOff = new FixedBackOff(1000, 3);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);
    errorHandler.addNotRetryableExceptions(JsonProcessingException.class);

    return errorHandler;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
      ConsumerFactory<String, String> consumerFactory, DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }
}
