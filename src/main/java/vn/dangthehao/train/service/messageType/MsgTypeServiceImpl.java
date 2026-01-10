package vn.dangthehao.train.service.messageType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.entity.MsgTypeSummary;
import vn.dangthehao.train.repository.MessageTypeRepository;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MsgTypeServiceImpl implements MessageTypeService {
  MessageTypeRepository msgTypeRepository;

  @Override
  public List<MsgTypeSummary> getMessageTypeByStatus(Long status) {
    return msgTypeRepository.findByActiveStatus(status);
  }
}
