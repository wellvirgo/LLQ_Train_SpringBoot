package vn.dangthehao.train.service.messageType;

import vn.dangthehao.train.entity.MsgTypeSummary;

import java.util.List;

public interface MessageTypeService {
  List<MsgTypeSummary> getMessageTypeByStatus(Long status);
}
