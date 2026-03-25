package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.UserActionLog;


@Repository
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
}
