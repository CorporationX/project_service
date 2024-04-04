package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ProjectRepository projectRepository;
    @Transactional
    public ResourceDto uploadFileToProject(Long userId, MultipartFile file) {
        TeamMember user = teamMemberRepository.findById(userId);
        Project project = user.getTeam().getProject();
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        int compareStorage = newStorageSize.compareTo(project.getMaxStorageSize());
        if (compareStorage > 0) {
            throw new RuntimeException("превышен максимальный размер файла");
        }
        String folder = project.getName() + project.getId();
        Resource resource = s3Client.uploadFile(file, folder);
        resource.setAllowedRoles(user.getRoles());
        resource.setCreatedBy(user);
        resource.setUpdatedBy(user);
        resource.setProject(project);
        resourceRepository.save(resource);
        project.setStorageSize(newStorageSize);
        project.getResources().add(resource);
        projectRepository.save(project);
        //projectService.updateProject(projectMapper.toDto(project));
        return resourceMapper.toDto(resource);
    }

//    public ResourceDto updateFileFromProject(Long user_id, Long resource_id, MultipartFile file){
//        TeamMember user = teamMemberRepository.findById(user_id);
//        Resource resource = resourceRepository.findById(resource_id).orElseThrow(() -> new EntityNotFoundException(String.format("Ресурс по id: %d не найден", resource_id)));
//        Project project = user.getTeam().getProject();
//        if(resource.getCreatedBy().getId() != user_id && project.getOwnerId() != user_id){
//            throw new RuntimeException("изменить файл может только создать файла или создать проекта");
//        }
//        long fileSize = file.getSize();
//        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(fileSize));
//        int compareStorage = newStorageSize.compareTo(project.getMaxStorageSize());
//        if (compareStorage > 0){
//            throw new RuntimeException("превышен максимальный размер файла");
//        }
//        String folder = resource.getKey().substring(0, resource.getKey().indexOf('/'));
//        Resource updatedResource = s3Client.uploadFile(file, folder);
//
//
//    }
}
