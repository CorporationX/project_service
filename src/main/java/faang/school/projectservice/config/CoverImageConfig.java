package faang.school.projectservice.config;

import faang.school.projectservice.dto.ImageConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "CoverImageConfig")
@ConfigurationProperties(prefix = "application.cover-image")
public class CoverImageConfig extends ImageConfig {

    public String toString() {
        final StringBuilder sb = new StringBuilder("CoverImageConfig{");
        sb.append("fileSizeMb=").append(fileSizeMb);
        sb.append(", squareSize=").append(squareSize);
        sb.append(", rectWidthSize=").append(rectWidthSize);
        sb.append(", rectHeightSize=").append(rectHeightSize);
        sb.append('}');
        return sb.toString();
    }
}
