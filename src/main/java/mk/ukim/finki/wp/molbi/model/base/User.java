package mk.ukim.finki.wp.molbi.model.base;

import jakarta.persistence.*;
import lombok.*;
import mk.ukim.finki.wp.molbi.model.enums.UserRole;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_user")
public class User {

    @Id
    private String id;

    private String alias;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
