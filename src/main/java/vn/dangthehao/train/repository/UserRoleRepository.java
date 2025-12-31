package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.AppUsersRole;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<AppUsersRole, Long> {
    List<AppUsersRole> findByUserId(Long userId);
}
