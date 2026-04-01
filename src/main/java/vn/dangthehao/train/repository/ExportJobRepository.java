package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.ExportJob;

@Repository
public interface ExportJobRepository extends JpaRepository<ExportJob, Long> {
  @Query("select ej.status from ExportJob ej where ej.id = :id")
  String findStatusById(Long id);
}
