package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceResponseDto;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import faang.school.projectservice.service.utilservice.ResourceUtilService;
import faang.school.projectservice.service.utilservice.TeamMemberUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceUtilService resourceUtilService;
    private final ResourceMapper resourceMapper;

    private final ProjectUtilService projectUtilService;
    private final TeamMemberUtilService teamMemberUtilService;

    private final S3Service s3Service;

    public ResourceResponseDto uploadNew(long userId, long projectId, MultipartFile multipartFile) {
        Project project = projectUtilService.getById(projectId);
        TeamMember creator = teamMemberUtilService.getByUserIdAndProjectId(userId, projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(multipartFile.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + "_" + project.getName();
        String key = s3Service.uploadFile(multipartFile, folder);

        Resource resource = Resource.builder()
                .name(multipartFile.getName())
                .key(key)
                .size(BigInteger.valueOf(multipartFile.getSize()))
                .allowedRoles(creator.getRoles().stream().toList())
                .type(ResourceType.getResourceType(multipartFile.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(creator)
                .updatedBy(creator)
                .project(project)
                .build();
        resource = resourceUtilService.save(resource);

        project.setStorageSize(newStorageSize);
        projectUtilService.save(project);

        return resourceMapper.toResponseDto(resource);
    }

    private void checkStorageSizeExceeded(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new ConflictException(String.format(
                    "Storage size was exceeded (%d/%d)", newStorageSize, maxStorageSize));
        }
    }
}
