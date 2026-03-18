package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.MsgTypeSummary;

import java.util.List;
import java.util.Set;

@Repository
public interface MessageTypeRepository
    extends JpaRepository<MsgTypeSummary, Long>, MessageTypeCustomRepository {
  List<MsgTypeSummary> findByActiveStatus(Long status);

  List<MsgTypeSummary> findByMsgTypeIn(Set<String> msgTypes);

  MsgTypeSummary findByMsgType(String msgType);
}
