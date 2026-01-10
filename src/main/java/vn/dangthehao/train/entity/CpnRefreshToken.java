package vn.dangthehao.train.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CPN_REFRESH_TOKEN")
public class CpnRefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq_generator")
  @SequenceGenerator(
      name = "refresh_token_seq_generator",
      sequenceName = "CPN_RF_TOKEN_SEQ",
      allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 512)
  @Column(name = "TOKEN", length = 512)
  private String token;

  @Column(name = "EXPIRED_AT")
  private LocalDate expiredAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "USER_ID")
  private AppUser user;

  @NotNull
  @ColumnDefault("0")
  @Column(name = "IS_REVOKED", nullable = false)
  private Boolean isRevoked = false;
}
