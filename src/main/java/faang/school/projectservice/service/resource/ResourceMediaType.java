package faang.school.projectservice.service.resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ResourceMediaType {
    public MediaType getMediaType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        return switch (extension) {
            case "jpeg", "jpg", "heif" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "xls" -> new MediaType("application", "vnd.ms-excel");
            case "docx" -> new MediaType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "mov", "mp4" -> new MediaType("video", "mp4");
            case "mp3" -> new MediaType("audio", "mpeg");
            case "mp4a" -> new MediaType("audio", "mp4");
            default -> MediaType.APPLICATION_OCTET_STREAM; // Fallback for unknown types
        };
    }

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1) ? fileName.substring(dotIndex + 1) : "";
    }
}
