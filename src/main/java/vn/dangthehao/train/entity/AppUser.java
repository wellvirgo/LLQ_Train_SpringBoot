package vn.dangthehao.train.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "APP_USER")
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 255)
  @NotNull
  @Column(name = "PASSWORD", nullable = false)
  private String password;

  @Size(max = 255)
  @NotNull
  @Column(name = "USERNAME", nullable = false)
  private String username;

  @Column(name = "ENABLED")
  private Boolean enabled;
}
