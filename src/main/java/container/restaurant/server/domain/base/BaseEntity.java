package container.restaurant.server.domain.base;

import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

/**
 * {@link Entity} 클래스의 기반 클래스
 * <p>
 * {@link Long} 타입의 <i>id</i> 속성을 제공한다.
 * <p>
 * 각 속성에 대해 {@code getter()} 를 제공한다.
 */
@Getter
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || id == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id=" + id +
                '}';
    }
}
