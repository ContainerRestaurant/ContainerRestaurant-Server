package container.restaurant.server.web.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.stream.Stream;

import static container.restaurant.server.web.util.CustomSortArgumentResolver.DEFAULT_ORDER;
import static container.restaurant.server.web.util.CustomSortArgumentResolver.SORT_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomSortArgumentResolverTest {

    @Test
    @DisplayName("기본 정렬")
    void testDefaultSort() {
        //given
        SortArgumentResolver resolver = new CustomSortArgumentResolver();
        MethodParameter parameter = mock(MethodParameter.class);
        NativeWebRequest webRequest = mock(NativeWebRequest.class);

        //then
        Sort sort = resolver.resolveArgument(parameter, null,
                webRequest, null);

        //when
        assertThat(sort).isEqualTo(Sort.by(DEFAULT_ORDER));
    }

    @ParameterizedTest(name = "유효[{index}]{argumentsWithNames}")
    @MethodSource
    void testValidSort(String params, Sort.Order res) {
        //given
        SortArgumentResolver resolver = new CustomSortArgumentResolver();
        MethodParameter parameter = mock(MethodParameter.class);
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getParameterValues(SORT_PARAMETER))
                .thenReturn(params.split(","));

        //then
        Sort sort = resolver.resolveArgument(parameter, null,
                webRequest, null);

        //when
        assertThat(sort).isEqualTo(Sort.by(DEFAULT_ORDER, res));
    }

    static Stream<Arguments> testValidSort() {
        return Stream.of(
                arguments("likeCount", Sort.Order.asc("likeCount")),
                arguments("likeCount,AsC", Sort.Order.asc("likeCount")),
                arguments("likeCount,asdf", Sort.Order.asc("likeCount")),
                arguments("likeCount,DesC", Sort.Order.desc("likeCount")),
                arguments("difficulty,asc", Sort.Order.asc("difficulty")),
                arguments("difficulty,descasd", Sort.Order.asc("difficulty")),
                arguments("difficulty,desc", Sort.Order.desc("difficulty")),
                arguments("difficulty,", Sort.Order.asc("difficulty"))
        );
    }

    @ParameterizedTest(name = "무효[{index}]{argumentsWithNames}")
    @MethodSource
    void testInvalidSort(String params) {
        //given
        SortArgumentResolver resolver = new CustomSortArgumentResolver();
        MethodParameter parameter = mock(MethodParameter.class);
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getParameterValues(SORT_PARAMETER))
                .thenReturn(params.split(","));

        //then
        Sort sort = resolver.resolveArgument(parameter, null,
                webRequest, null);

        //when
        assertThat(sort).isEqualTo(Sort.by(DEFAULT_ORDER));
    }

    static Stream<Arguments> testInvalidSort() {
        return Stream.of(
                arguments("asdf"),
                arguments("createdDate"),
                arguments("ASC"),
                arguments("DESC")
        );
    }

}