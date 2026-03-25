package vn.dangthehao.train.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.entity.UserActionLog;
import vn.dangthehao.train.service.kafka.UserActionLogConsumer;
import vn.dangthehao.train.service.userLog.UserLogService;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserActionLogController {
  UserActionLogConsumer userActionLogConsumer;
  UserLogService userLogService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<UserActionLog>>> getUserActionLogs() {
    return ResponseEntity.ok(ApiResponseBuilder.success(userLogService.getUserActionLogs()));
  }

  @PostMapping("/replay")
  public ResponseEntity<Void> reConsumeUserActivityLog() {
    //    userActionLogConsumer.reConsumerUserActivityLogByOffset(0, 0);
    userActionLogConsumer.reConsumerUserActivityLogByTimestamp(0, 1773910845000L);
    return ResponseEntity.accepted().build();
  }
}
