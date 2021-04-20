package container.restaurant.server.domain.base;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

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

}
