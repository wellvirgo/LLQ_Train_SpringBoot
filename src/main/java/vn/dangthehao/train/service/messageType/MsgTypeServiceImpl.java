package vn.dangthehao.train.service.messageType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.dto.messageType.MsgTypeCreateRequest;
import vn.dangthehao.train.dto.messageType.MsgTypeResponse;
import vn.dangthehao.train.entity.MsgTypeSummary;
import vn.dangthehao.train.mapper.MsgTypeMapper;
import vn.dangthehao.train.repository.MessageTypeRepository;

import java.util.*;
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

  @Override
  public String addMultiMessageType(MsgTypeCreateRequest[] request) {
    List<MsgTypeSummary> msgTypeSummaryList = new ArrayList<>();
    for (MsgTypeCreateRequest msgTypeCreateRequest : request) {
      MsgTypeSummary msgTypeSummary = msgTypeMapper.toMsgTypeSummary(msgTypeCreateRequest);
      msgTypeSummaryList.add(msgTypeSummary);
    }
    return msgTypeRepository.batchCreateMessageType(msgTypeSummaryList);
  }

  private MsgTypeResponse buildMsgTypeResponse(MsgTypeSummary msgTypeSummary) {
    return MsgTypeResponse.builder()
        .msgType(msgTypeSummary.getMsgType())
        .description(msgTypeSummary.getDescription())
        .build();
  }
}
