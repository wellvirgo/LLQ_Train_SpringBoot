package vn.dangthehao.train.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.dto.messageType.MsgTypeCreateRequest;
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

  @PostMapping
  public ResponseEntity<ApiResponse<String>> addMultiMessageType(@RequestBody MsgTypeCreateRequest[] request) {
    return ResponseEntity.ok(ApiResponseBuilder.success(msgTypeService.addMultiMessageType(request)));
  }
}
