package faang.school.projectservice.enums;

import java.util.Arrays;
import java.util.Set;

public enum ResourceType {
    DOCUMENT("document", Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt")),
    IMAGE("image", Set.of("jpg", "jpeg", "png", "gif", "bmp", "svg", "webp")),
    VIDEO("video", Set.of("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm")),
    AUDIO("audio", Set.of("mp3", "wav", "flac", "aac", "ogg", "wma")),
    ARCHIVE("archive", Set.of("zip", "rar", "7z", "tar", "gz")),
    OTHER("other", Set.of());

    private final String type;
    private final Set<String> extensions;

    ResourceType(String type, Set<String> extensions) {
        this.type = type;
        this.extensions = extensions;
    }

    public static ResourceType fromExtension(String extension) {
        String ext = extension.toLowerCase();
        return Arrays.stream(values())
                .filter(type -> type.extensions.contains(ext))
                .findFirst()
                .orElse(OTHER);
    }
}