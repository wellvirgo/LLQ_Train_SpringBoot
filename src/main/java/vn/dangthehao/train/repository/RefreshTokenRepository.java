package vn.dangthehao.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.entity.CpnRefreshToken;

import java.time.LocalDate;

@Repository
public interface RefreshTokenRepository extends JpaRepository<CpnRefreshToken, Long> {
    boolean existsByTokenAndIsRevokedFalseAndExpiredAtAfter(String token, LocalDate now);

    @Modifying
    @Query("update CpnRefreshToken rf set rf.isRevoked = true where rf.token = :token")
    void updateIsRevoked(String token);
}
