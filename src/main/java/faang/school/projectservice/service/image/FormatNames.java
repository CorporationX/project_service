package faang.school.projectservice.service.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum FormatNames {
    PNG("image/png", "PNG"),
    GIF("image/gif", "GIF");

    private final String contentType;
    private final String format;
    private static final String DEFAULT_FORMAT = "JPEG";

    private static final Map<String, String> FORMAT_MAP = Stream.of(values())
            .collect(Collectors.toMap(
                    FormatNames::getContentType,
                    FormatNames::getFormat));

    public static String getFormatByContentType(String contentType) {
        return FORMAT_MAP.getOrDefault(contentType, DEFAULT_FORMAT);
    }
}
