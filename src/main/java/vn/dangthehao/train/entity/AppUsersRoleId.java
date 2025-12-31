package vn.dangthehao.train.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@Embeddable
public class AppUsersRoleId implements java.io.Serializable {
  private static final long serialVersionUID = -6384871337580641251L;

  @NotNull
  @Column(name = "USER_ID", nullable = false)
  private Long userId;

  @NotNull
  @Column(name = "ROLE_ID", nullable = false)
  private Long roleId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    AppUsersRoleId entity = (AppUsersRoleId) o;
    return Objects.equals(this.roleId, entity.roleId) && Objects.equals(this.userId, entity.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roleId, userId);
  }
}
