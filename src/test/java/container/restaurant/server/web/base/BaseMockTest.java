package container.restaurant.server.web.base;

import container.restaurant.server.domain.base.BaseCreatedTimeEntity;
import container.restaurant.server.domain.base.BaseEntity;
import container.restaurant.server.domain.base.BaseTimeEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import java.util.function.Supplier;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class BaseMockTest {

    private AutoCloseable closeable;

    @BeforeEach
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @NotNull
    protected  <T extends BaseEntity> T makeEntity(long id, Supplier<T> supplier) {
        T entity = spy(supplier.get());

        // Base 클래스의 속성을 세팅
        when(entity.getId()).thenReturn(id);
        if (entity instanceof BaseCreatedTimeEntity)
            when(((BaseCreatedTimeEntity) entity).getCreatedDate()).thenReturn(now());
        if (entity instanceof BaseTimeEntity)
            when(((BaseTimeEntity) entity).getModifiedDate()).thenReturn(now());

        return entity;
    }
}
