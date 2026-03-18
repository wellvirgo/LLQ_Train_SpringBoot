package vn.dangthehao.train.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "MSG_TYPE_SUMMARY")
public class MsgTypeSummary {
  @Id
  @Size(max = 100)
  @Column(name = "MSG_TYPE", nullable = false, length = 100)
  private String msgType;

  @Size(max = 100)
  @Column(name = "DESCRIPTION", length = 100)
  private String description;

  @NotNull
  @ColumnDefault("1")
  @Column(name = "ACTIVE_STATUS", nullable = false)
  @Builder.Default
  private Long activeStatus = 1L;
}
