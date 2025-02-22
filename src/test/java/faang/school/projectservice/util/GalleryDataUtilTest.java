package faang.school.projectservice.util;

import faang.school.projectservice.dto.gallery.GalleryResponseDto;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

public class GalleryDataUtilTest {

    public static GalleryResponseDto getGalleryResponseDto() {
        return new GalleryResponseDto(1, List.of("key1", "key2"));
    }
    public static List<MockMultipartFile> getMultipartFiles() {
        MockMultipartFile file1 = new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.txt", "text/plain", "File 2 content".getBytes());
        return List.of(file1, file2);
    }
}
