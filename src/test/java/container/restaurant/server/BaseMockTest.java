package container.restaurant.server;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public abstract class BaseMockTest {

    @BeforeEach
    void setMock() {
        MockitoAnnotations.openMocks(this);
    }

}
