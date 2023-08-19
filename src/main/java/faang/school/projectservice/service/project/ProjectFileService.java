package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exceptions.InvalidCurrentUserException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFileService {
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final FileService fileService;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto upload(MultipartFile multipartFile, long projectId, long teamMemberId) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        validateTeamMember(project, teamMemberId);

        String objectKey = fileService.upload(multipartFile, projectId);

        Resource resource = Resource.builder()
                .name(multipartFile.getName())
                .key(objectKey)
                .size(BigInteger.valueOf(multipartFile.getSize()))
                .type(ResourceType.getResourceType(multipartFile.getContentType()))
                .createdBy(teamMember)
                .project(project)
                .build();

        updateProjectStorage(resource);
        resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public void delete(long resourceId, long teamMemberId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        validateTeamMember(resource.getProject(), teamMemberId);

        if (canDeleteResource(resource, teamMemberId)) {
            fileService.delete(resource.getKey());

            resource.setStatus(ResourceStatus.DELETED);
            resourceRepository.save(resource);
            updateProjectStorage(resource);
        }
    }

    private void validateTeamMember(Project project, long teamMemberId) {
        List<Long> projectMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .distinct()
                .toList();

        if (!projectMembers.contains(teamMemberId)) {
            String errorMessage = String.format("The team member with id: %d is not on the project", teamMemberId);
            throw new InvalidCurrentUserException(errorMessage);
        }
    }

    private void updateProjectStorage(Resource resource) {
        Project project = resource.getProject();
        BigInteger storageSize = project.getStorageSize();
        BigInteger resourceSize = resource.getSize();

        if (resource.getStatus().equals(ResourceStatus.DELETED)) {
            project.setStorageSize(storageSize.add(resourceSize));
        } else if (resourceSize.compareTo(storageSize) <= 0) {
            project.setStorageSize(storageSize.subtract(resourceSize));
        }

        projectRepository.save(project);
    }


    private boolean canDeleteResource(Resource resource, long teamMemberId) {
        return userIsProjectManager(resource.getProject(), teamMemberId) ||
                userIsFileCreator(resource, teamMemberId) &&
                        !resource.getStatus().equals(ResourceStatus.DELETED);
    }

    private boolean userIsProjectManager(Project project, long teamMemberId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .anyMatch(teamMember ->
                        teamMember.getRoles().contains(TeamRole.MANAGER) && teamMember.getId().equals(teamMemberId));
    }

    private boolean userIsFileCreator(Resource resource, long teamMemberId) {
        return resource.getCreatedBy().getId().equals(teamMemberId);
    }
}
