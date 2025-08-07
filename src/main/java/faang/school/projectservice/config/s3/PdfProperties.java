package faang.school.projectservice.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pdf")
public record PdfProperties(
        String templateName,
        TempFile tempFile
) {
    public record TempFile(String prefix, String suffix) {}
}
