package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.CpnRefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<CpnRefreshToken, Long> {}
