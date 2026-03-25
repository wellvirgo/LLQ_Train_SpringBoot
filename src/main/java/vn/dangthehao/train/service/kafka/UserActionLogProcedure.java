package vn.dangthehao.train.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.train.dto.common.EventEnvelope;
import vn.dangthehao.train.entity.UserActionLog;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserActionLogProcedure {
  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;
  static String TOPIC = "user-activity-topic";

@Transactional("kafkaTransactionManager")
  public void sendUserActionLog(UserActionLog userActionLog) {
    try {
      EventEnvelope envelope =
          EventEnvelope.builder().eventType("USER_ACTION_LOG").payload(userActionLog).build();
      kafkaTemplate.send(TOPIC, "log-test", objectMapper.writeValueAsString(envelope));
//      throw new RuntimeException("Sending user action log failed");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}
