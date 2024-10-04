package faang.school.projectservice.client;

import faang.school.projectservice.config.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            String userId = ((ServletRequestAttributes) requestAttributes).getRequest().getHeader("x-user-id");
            if (userId != null) {
                template.header("x-user-id", String.valueOf(userContext.getUserId()));
            }
        }
    }
}
