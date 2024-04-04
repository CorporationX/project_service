package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final S3Service s3Client;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectService projectService;
    private final ResourceMapper resourceMapper;
    private final ProjectMapper projectMapper;

    public ResourceDto uploadFileToProject(Long userId, MultipartFile file) {
        TeamMember user = teamMemberRepository.findById(userId);
        Project project = user.getTeam().getProject();
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        int compareStorage = newStorageSize.compareTo(project.getMaxStorageSize());
        if (compareStorage > 0) {
            throw new RuntimeException("превышен максимальный размер файла");
        }
        String folder = project.getName() + project.getId();
        Resource resource = s3Client.uploadDile(file, folder);
        resource.setAllowedRoles(user.getRoles());
        resource.setCreatedBy(user);
        resource.setUpdatedBy(user);
        resource.setProject(project);
        resourceRepository.save(resource);
        project.setStorageSize(newStorageSize);
        project.getResources().add(resource);
        projectService.updateProject(projectMapper.toDto(project));
        return resourceMapper.toDto(resource);
    }

    public ResourceDto updateFileFromProject(Long user_id, Long resource_id, MultipartFile file){
        TeamMember user = teamMemberRepository.findById(user_id);
        return null;
    }
}
