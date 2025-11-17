package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.PmhComponents1;

@Repository
public interface PmhComponents1Repository
    extends JpaRepository<PmhComponents1, Long>,
        JpaSpecificationExecutor<PmhComponents1>,
        PmhComponents1CustomRepository {}
