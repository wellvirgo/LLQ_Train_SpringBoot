package vn.dangthehao.train.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "EXPORT_JOB")
public class ExportJob {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "USER_ID", nullable = false)
  private AppUser user;

  @Lob
  @Column(name = "FILTER_PAYLOAD")
  private String filterPayload;

  @Size(max = 4000)
  @Column(name = "EXPORT_URL", length = 4000)
  private String exportUrl;

  @Builder.Default
  @Size(max = 128)
  @Column(name = "STATUS", length = 128)
  private String status = "PENDING";

  @Size(max = 4000)
  @Column(name = "FILE_NAME", length = 4000)
  private String fileName;

  @CreatedDate
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "CREATED_AT")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "UPDATED_AT")
  private LocalDateTime updatedAt;
}
