package container.restaurant.server.web.util;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Set;

@Component
public class CustomSortArgumentResolver implements SortArgumentResolver {

    protected static final String SORT_PARAMETER = "sort";
    protected static final String DEFAULT_ATTR = "createdDate";
    protected static final Sort.Order DEFAULT_ORDER = Sort.Order.desc(DEFAULT_ATTR);

    protected static final Set<String> AVAILABLE_SORT_ATTR = Set.of(
            "likeCount", "difficulty"
    );

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Sort.class.equals(parameter.getParameterType());
    }

    @Override
    @NonNull
    public Sort resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String[] params = webRequest.getParameterValues(SORT_PARAMETER);

        if (params != null && AVAILABLE_SORT_ATTR.contains(params[0])) {
            if (params.length != 1 && "DESC".equalsIgnoreCase(params[1])) {
                return Sort.by(DEFAULT_ORDER, Sort.Order.desc(params[0]));
            } else {
                return Sort.by(DEFAULT_ORDER, Sort.Order.asc(params[0]));
            }
        }
        return Sort.by(DEFAULT_ORDER);
    }

}
