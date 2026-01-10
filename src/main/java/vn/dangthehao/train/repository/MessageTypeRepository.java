package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.MsgTypeSummary;

import java.util.List;

@Repository
public interface MessageTypeRepository extends JpaRepository<MsgTypeSummary, Long> {
  List<MsgTypeSummary> findByActiveStatus(Long status);
}
