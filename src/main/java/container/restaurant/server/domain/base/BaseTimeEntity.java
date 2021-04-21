package container.restaurant.server.domain.base;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * {@link Entity} 클래스의 기반 클래스
 * <p>
 * {@link Long} 타입의 <i>id</i>, {@link LocalDateTime} 타입의 <i>createdDate</i>,
 * {@link LocalDateTime} 타입의 <i>modifiedDate</i> 속성을 제공한다.
 * <p>
 * 각 속성에 대해 {@code getter()} 를 제공한다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity extends BaseCreatedTimeEntity {

    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
