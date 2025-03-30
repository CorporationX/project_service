package faang.school.projectservice.client;

import faang.school.projectservice.config.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*@Slf4j
@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        Long userId = userContext.getUserId();
        if (userId != null) {
            template.header("x-user-id", String.valueOf(userId));
        } else {
            log.warn("User ID is null, skipping header");
        }
    }

    /*public void apply(RequestTemplate template) {
        template.header("x-user-id", String.valueOf(userContext.getUserId()));
    }
}*/
