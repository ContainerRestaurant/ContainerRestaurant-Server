package container.restaurant.server.domain.feed.picture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ImageFileDtoTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("생성 테스트")
    void createTest(String imageName, MediaType expectedType) {
        //given
        InputStream is = mock(InputStream.class);

        //when
        final ImageFileDto result = ImageFileDto.from(is, imageName);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getImageType()).isEqualTo(expectedType);
    }

    static Stream<Arguments> createTest() {
        return Stream.of(
                Arguments.arguments("png", MediaType.IMAGE_PNG),
                Arguments.arguments("jpeg", MediaType.IMAGE_JPEG),
                Arguments.arguments("jpg", MediaType.IMAGE_JPEG)
        );
    }

}