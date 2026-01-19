package vn.dangthehao.train.service.messageType;

import vn.dangthehao.train.dto.messageType.MsgTypeResponse;
import vn.dangthehao.train.entity.MsgTypeSummary;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MessageTypeService {
  List<MsgTypeSummary> getMessageTypeByStatus(Long status);
  Map<String, MsgTypeResponse> getMessageTypeMapByMsgTypes(Set<String> msgTypes);
}
