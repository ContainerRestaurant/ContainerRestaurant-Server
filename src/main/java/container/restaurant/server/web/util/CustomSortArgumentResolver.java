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

    /**
     * [요구사항]
     *  default - 최신순, 좋아요/난이도 동점시 - 최신순
     *  가능한 케이스 - 오래된 순(createdDate,asc)
     **/
    @Override
    @NonNull
    public Sort resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String[] params = webRequest.getParameterValues(SORT_PARAMETER);

        if (params != null) {
            String[] param=params[0].split(",");

            if (AVAILABLE_SORT_ATTR.contains(param[0])){ // [CASE 좋아요/난이도] - 동점시 최신순
                boolean desc_flag=false;
                if(param.length>1){ // desc/asc 안 들어오거나 asc일 경우 false
                    if(param[1].equalsIgnoreCase("desc"))
                        desc_flag=true;
                }
                if(desc_flag){ // 내림차순, 동점처리
                    return Sort.by(Sort.Order.desc(param[0]),DEFAULT_ORDER);
                }
                else{ // 오름차순, 동점처리
                    return Sort.by(Sort.Order.asc(param[0]),DEFAULT_ORDER);
                }
            }
            else if(param.length>1 && param[0].equals(DEFAULT_ATTR)){
                if(param[1].equalsIgnoreCase("asc")){ //[CASE 오래된 순]
                    return Sort.by(Sort.Order.asc(param[0]));
                }
            }
        }
        //[CASE default/invalid 최신순]
        return Sort.by(DEFAULT_ORDER);
    }

}
