package faang.school.projectservice.mapper;

import faang.school.projectservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Named("UserMapperUtil")
@Component
@RequiredArgsConstructor
public class ProjectMapperUtil {
    private final UserContext userContext;

    @Named("setOwner")
    public Long setOwner(Long ownerId) {
        if (Objects.isNull(ownerId)) {
            return userContext.getUserId();
        }
        return ownerId;
    }
}
