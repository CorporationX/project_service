package faang.school.projectservice.util.project;

import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

public class ProjectFabric {

    public static Resource buildResource(BigInteger size) {
        return Resource
                .builder()
                .size(size)
                .build();
    }

    public static Resource buildResource(String key) {
        return Resource
                .builder()
                .key(key)
                .build();
    }

    public static Project buildProject(Long id) {
        return Project
                .builder()
                .id(id)
                .build();
    }

    public static Project buildProject(Long id, List<Resource> resources) {
        return Project
                .builder()
                .id(id)
                .resources(resources)
                .build();
    }

    public static Project buildProject(Long id, Long ownerId) {
        return Project
                .builder()
                .id(id)
                .ownerId(ownerId)
                .build();
    }

    public static Project buildProjectCoverImageId(Long id, String coverImageId) {
        return Project
                .builder()
                .id(id)
                .coverImageId(coverImageId)
                .build();
    }

    public static Project buildProject(Long id, BigInteger storageSize) {
        return Project
                .builder()
                .id(id)
                .storageSize(storageSize)
                .build();
    }

    public static Project buildProject(Long id, BigInteger storageSize, BigInteger maxStorageSize) {
        return Project
                .builder()
                .id(id)
                .storageSize(storageSize)
                .maxStorageSize(maxStorageSize)
                .build();
    }

    public static Project buildProjectName(Long id, String name) {
        return Project
                .builder()
                .id(id)
                .name(name)
                .build();
    }

    public static MultipartFile buildMultiPartFile() {
        return new MockMultipartFileForTest();
    }

    public static MultipartFile buildMultiPartFile(Long size) {
        return new MockMultipartFileForTest(null, null, null, null, size, null, null);
    }

    public static MultipartFile buildMultiPartFile(Long size, String type) {
        return new MockMultipartFileForTest(null, null, type, null, size, null, null);
    }

    public static MultipartFile buildMultiPartFile(String originalName, Long size, String type,
                                                   InputStream inputStream) {
        return new MockMultipartFileForTest(null, originalName, type, null, size, null, inputStream);
    }

    public static ResourceDownloadDto buildResourceDownloadDto(byte[] bytes, String contentType,
                                                               ContentDisposition contentDisposition) {
        return ResourceDownloadDto
                .builder()
                .bytes(bytes)
                .type(MediaType.valueOf(contentType))
                .contentDisposition(contentDisposition)
                .build();
    }
}
