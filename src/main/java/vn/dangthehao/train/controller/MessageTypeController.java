package vn.dangthehao.train.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.entity.MsgTypeSummary;
import vn.dangthehao.train.service.messageType.MessageTypeService;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/message-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageTypeController {
  MessageTypeService msgTypeService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<MsgTypeSummary>>> getMessageTypeByStatus(
      @RequestParam("status") Long status) {
    ApiResponse<List<MsgTypeSummary>> apiResponse =
        ApiResponseBuilder.success(msgTypeService.getMessageTypeByStatus(status));

    return ResponseEntity.ok(apiResponse);
  }
}
