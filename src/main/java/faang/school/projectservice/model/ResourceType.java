package faang.school.projectservice.model;

import static java.util.Objects.isNull;
public enum ResourceType {
    NONE,
    VIDEO,
    AUDIO,
    IMAGE,
    TEXT,
    PDF,
    MSWORD,
    MSEXCEL,
    ZIP,
    OTHER;

    public static ResourceType getResourceType(String contentType) {
        if (isNull(contentType)) {
            return ResourceType.NONE;
        } else if (contentType.contains("image")) {
            return ResourceType.IMAGE;
        } else if (contentType.contains("video")) {
            return ResourceType.VIDEO;
        } else if (contentType.contains("audio")) {
            return ResourceType.AUDIO;
        } else if (contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return ResourceType.MSWORD;
        } else if (contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return ResourceType.MSEXCEL;
        } else if (contentType.contains("pdf")) {
            return ResourceType.PDF;
        } else if (contentType.contains("zip")) {
            return ResourceType.ZIP;
        } else if (contentType.contains("text")) {
            return ResourceType.TEXT;
        } else {
            return ResourceType.OTHER;
        }
    }
}
