package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.PmhComponents1;

import java.util.List;

@Repository
public interface PmhComponents1Repository
    extends JpaRepository<PmhComponents1, Long>,
        JpaSpecificationExecutor<PmhComponents1>,
        PmhComponents1CustomRepository {
    @Modifying
    @Query("update PmhComponents1 c set c.status = :status where c.id in :ids")
    int updateStatusForIds(List<Long> ids, long status);
}
