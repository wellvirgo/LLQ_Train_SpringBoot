package vn.dangthehao.train.service.messageType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.dto.messageType.MsgTypeResponse;
import vn.dangthehao.train.entity.MsgTypeSummary;
import vn.dangthehao.train.mapper.MsgTypeMapper;
import vn.dangthehao.train.repository.MessageTypeRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MsgTypeServiceImpl implements MessageTypeService {
  MessageTypeRepository msgTypeRepository;
  MsgTypeMapper msgTypeMapper;

  @Override
  public List<MsgTypeSummary> getMessageTypeByStatus(Long status) {
    return msgTypeRepository.findByActiveStatus(status);
  }

  @Override
  public Map<String, MsgTypeResponse> getMessageTypeMapByMsgTypes(Set<String> msgTypes) {
    if (msgTypes == null || msgTypes.isEmpty()) {
      return Collections.emptyMap();
    }
    return msgTypeRepository.findByMsgTypeIn(msgTypes).stream()
        .collect(Collectors.toMap(MsgTypeSummary::getMsgType, this::buildMsgTypeResponse));
  }

  @Override
  public MsgTypeResponse getMessageTypeByMsgType(String msgType) {
    MsgTypeSummary msgTypeSummary = msgTypeRepository.findByMsgType(msgType);
    return msgTypeMapper.toMsgTypeResponse(msgTypeSummary);
  }

  private MsgTypeResponse buildMsgTypeResponse(MsgTypeSummary msgTypeSummary) {
    return MsgTypeResponse.builder()
        .msgType(msgTypeSummary.getMsgType())
        .description(msgTypeSummary.getDescription())
        .build();
  }
}
