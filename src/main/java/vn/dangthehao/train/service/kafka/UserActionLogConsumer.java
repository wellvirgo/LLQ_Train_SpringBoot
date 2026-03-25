package vn.dangthehao.train.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.dto.common.EventEnvelope;
import vn.dangthehao.train.entity.UserActionLog;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.service.userLog.UserLogService;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserActionLogConsumer implements ConsumerSeekAware {
  UserLogService userLogService;
  ObjectMapper objectMapper;
  @NonFinal ConsumerSeekCallback seekCallback;

  @Override
  public void registerSeekCallback(ConsumerSeekCallback seekCallback) {
    this.seekCallback = seekCallback;
  }

  @KafkaListener(topics = "user-activity-topic", groupId = "log-group")
  public void consumeUserActivityLog(ConsumerRecord<String, String> record, Acknowledgment ack) {
    try {
      EventEnvelope envelope = objectMapper.readValue(record.value(), EventEnvelope.class);

      switch (envelope.getEventType()) {
        case "USER_ACTION_LOG":
          {
            UserActionLog userActionLog =
                objectMapper.convertValue(envelope.getPayload(), UserActionLog.class);
            userLogService.addUserActionLog(userActionLog);
            break;
          }
        default:
          log.warn("Unknown event type: {}", envelope.getEventType());
      }

      ack.acknowledge();

    } catch (Exception e) {
      log.error("Error {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  public void reConsumerUserActivityLogByOffset(int partition, long offset) {
    final String topic = "user-activity-topic";
    if (this.seekCallback != null) {
      this.seekCallback.seek(topic, partition, offset);
    } else {
      log.error("Seek callback is null, offset = {}", offset);
      throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, " Error with kafka");
    }
  }

  public void reConsumerUserActivityLogByTimestamp(int partition, long timestamp) {
    final String topic = "user-activity-topic";
    if (this.seekCallback != null) {
      this.seekCallback.seekToTimestamp(topic, partition, timestamp);
    } else {
      log.error("Seek callback is null, timestamp={}", timestamp);
      throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, " Error with kafka");
    }
  }

  //  public void reConsumeUserActivityLog(int partition, long offset) {
  //    userActionLogs.clear();
  //    final String topic = "user-activity-topic";
  //    ConsumerSeekCallback callback =
  //        Objects.requireNonNull(getSeekCallbacksFor(new TopicPartition(topic, partition)))
  //            .getFirst();
  //    if (callback != null) {
  //      callback.seek(topic, partition, offset);
  //    } else {
  //      log.error("Partition {} not assigned", partition);
  //    }
  //  }

}
