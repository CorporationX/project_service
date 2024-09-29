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
        } else if (contentType.contains("msword")) {
            return ResourceType.MSWORD;
        } else if (contentType.contains("ms-excel")) {
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
//
//    public static String getContentType(ResourceType resourceType) {
//        if (isNull(resourceType)) {
//            return "";
//        } else if (resourceType == ResourceType.IMAGE) {
//            return "image/"
//        } else if (resourceType.contains("video")) {
//            return ResourceType.VIDEO;
//        } else if (resourceType.contains("audio")) {
//            return ResourceType.AUDIO;
//        } else if (resourceType.contains("msword")) {
//            return ResourceType.MSWORD;
//        } else if (resourceType.contains("ms-excel")) {
//            return ResourceType.MSEXCEL;
//        } else if (resourceType.contains("pdf")) {
//            return ResourceType.PDF;
//        } else if (resourceType.contains("zip")) {
//            return ResourceType.ZIP;
//        } else if (resourceType.contains("text")) {
//            return ResourceType.TEXT;
//        } else {
//            return ResourceType.OTHER;
//        }
//    }
}
