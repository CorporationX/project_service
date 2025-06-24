package faang.school.projectservice.validation.gallery;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "gallery")
@Getter
@Setter
public class GalleryProperties {
    private int maxLimit;
    private List<String> allowedContentTypes;
}