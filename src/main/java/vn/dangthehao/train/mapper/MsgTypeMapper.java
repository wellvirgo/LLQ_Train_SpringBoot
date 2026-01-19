package vn.dangthehao.train.mapper;

import org.mapstruct.Mapper;
import vn.dangthehao.train.dto.messageType.MsgTypeResponse;
import vn.dangthehao.train.entity.MsgTypeSummary;

@Mapper(componentModel = "spring")
public interface MsgTypeMapper {
  MsgTypeResponse toMsgTypeResponse(MsgTypeSummary entity);
}
