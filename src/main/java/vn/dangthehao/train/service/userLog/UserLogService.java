package vn.dangthehao.train.service.userLog;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.train.entity.UserActionLog;
import vn.dangthehao.train.repository.UserActionLogRepository;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserLogService {
  UserActionLogRepository userActionLogRepository;

  @Transactional
  public void addUserActionLog(UserActionLog userActionLog) {
    userActionLogRepository.save(userActionLog);
  }

  public List<UserActionLog> getUserActionLogs() {
    return userActionLogRepository.findAll(Sort.by("id").ascending());
  }
}
