package faang.school.projectservice.service.resource;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.resource.ProjectResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectResourceServiceImpl implements ProjectResourceService {

    private static final String BASE_FOLDER_PATH = "project" + File.separator;

    private final TeamMemberJpaRepository teamMemberRepository;
    private final AmazonS3Service amazonS3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ProjectResourceValidator projectResourceValidator;

    @Override
    @Transactional
    public ResourceDto saveFile(long userId, long projectId, MultipartFile file) {
        TeamMember teamMember = findTeamMemberByUserIdAndProjectId(userId, projectId);
        Project project = teamMember.getTeam().getProject();

        projectResourceValidator.validateMaxStorageSize(project, file.getSize());

        String key = amazonS3Service.uploadFile(BASE_FOLDER_PATH + projectId, file);
        Resource resource = buildResource(key, file, teamMember);
        resource = resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource getFile(long userId, long projectId, long resourceId) {
        Resource resource = findResourceById(resourceId);
        findTeamMemberByUserIdAndProjectId(userId, projectId);

        return amazonS3Service.getFile(resource.getKey());
    }

    @Override
    public void deleteFile(long userId, long projectId, long resourceId) {
        Resource resource = findResourceById(resourceId);
        TeamMember teamMember = findTeamMemberByUserIdAndProjectId(userId, projectId);

        projectResourceValidator.validateDeletePermission(teamMember, resource);

        amazonS3Service.deleteFile(resource.getKey());

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);

        resourceRepository.save(resource);
    }

    private Resource buildResource(String key, MultipartFile file, TeamMember creator) {
        return Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(List.copyOf(creator.getRoles()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(creator)
                .updatedBy(creator)
                .project(creator.getTeam().getProject())
                .build();
    }

    private Resource findResourceById(long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", id)));
    }

    private TeamMember findTeamMemberByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with User ID %s and Project UD %s not found.", userId, projectId)));
    }
}