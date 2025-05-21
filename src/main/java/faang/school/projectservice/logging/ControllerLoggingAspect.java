package faang.school.projectservice.logging;

import faang.school.projectservice.config.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerLoggingAspect {
    private final UserContext userContext;

    @Around("within(faang.school.projectservice.controller..*)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest req =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String uri = req.getRequestURI() +
                (req.getQueryString() != null ? "?" + req.getQueryString() : "");
        String method = req.getMethod();
        Long userId = userContext.getUserId();

        log.info("IN [{}] {} userId={} params={}",
                method, uri, userId, req.getQueryString());

        try {
            Object result = pjp.proceed();
            log.info("OUT [{}] {} userId={} durationMs={}",
                    method, uri, userId, System.currentTimeMillis() - start);
            return result;
        } catch (Exception ex) {
            log.warn("ERR [{}] {} userId={} ex={} message={} durationMs={}",
                    method, uri, userId, ex.getClass().getSimpleName(),
                    ex.getMessage(), System.currentTimeMillis() - start);
            throw ex;
        }
    }
}
