package container.restaurant.server.web.util;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CustomSortArgumentResolver extends SortHandlerMethodArgumentResolver {

    @Override
    @NonNull
    protected Sort getDefaultFromAnnotationOrFallback(@NonNull MethodParameter parameter) {
        return Sort.by(Sort.Direction.DESC, "createdDate");
    }
}
