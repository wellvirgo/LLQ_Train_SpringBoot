package vn.dangthehao.train.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "USER_ACTION_LOG")
public class UserActionLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 512)
  @Column(name = "ACTION", length = 512)
  private String action;

  @Size(max = 4000)
  @Column(name = "LOG", length = 4000)
  private String log;

  @ColumnDefault("systimestamp")
  @Column(name = "TIMESTAMP")
  private LocalDateTime timestamp;
}
