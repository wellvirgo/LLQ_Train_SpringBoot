package vn.dangthehao.train.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PMH_COMPONENTS_1")
public class PmhComponents1 {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pmh_component_seq_generator")
  @SequenceGenerator(
          name = "pmh_component_seq_generator",
          sequenceName = "PMH_COMPONENTS_1_SEQ",
          allocationSize = 1
  )
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 20)
  @Column(name = "COMPONENT_CODE", length = 20)
  private String componentCode;

  @Size(max = 150)
  @Column(name = "COMPONENT_NAME", length = 150)
  private String componentName;

  @Size(max = 1500)
  @Column(name = "MESSAGE_TYPE", length = 1500)
  private String messageType;

  @Size(max = 1000)
  @Column(name = "CONNECTION_METHOD", length = 1000)
  private String connectionMethod;

  @Size(max = 2)
  @Column(name = "CHECK_TOKEN", length = 2)
  private String checkToken;

  @Column(name = "IS_DISPLAY")
  private Long isDisplay;

  @Column(name = "STATUS")
  private Long status;

  @Column(name = "IS_ACTIVE")
  private Long isActive;

  @Size(max = 4000)
  @Column(name = "NEW_DATA", length = 4000)
  private String newData;

  @Column(name = "EFFECTIVE_DATE")
  private LocalDate effectiveDate;

  @Column(name = "END_EFFECTIVE_DATE")
  private LocalDate endEffectiveDate;
}
