package container.restaurant.server.domain.base;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * {@link Entity} 클래스의 기반 클래스
 * <p>
 * {@link Long} 타입의 <i>id</i>, {@link LocalDateTime} 타입의 <i>createdDate</i> 속성을 제공한다.
 * <p>
 * 각 속성에 대해 {@code getter()} 를 제공한다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseCreatedTimeEntity extends BaseEntity {

    @CreatedDate
    private LocalDateTime createdDate;

}
