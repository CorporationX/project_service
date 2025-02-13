package faang.school.projectservice.service;

import faang.school.projectservice.config.minio.MinioProperties;
import faang.school.projectservice.dto.resource.ResourceCreateDto;
import faang.school.projectservice.dto.resource.ResourceResultDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.mapper.ResourceResultMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.minio.MinioServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MinioServiceV2 minioService;
    private final MinioProperties minioProperties;
    private final ResourceMapper resourceMapper;
    private final ResourceResultMapper resourceResultMapper;

    public Resource getResourceRefById(long id) {
        return resourceRepository.getReferenceById(id);
    }

    public ResourceResultDto uploadResource(MultipartFile file, Long projectId, Long teamMemberId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Проект не найден"));

        BigInteger currentSize = project.getStorageSize() == null ? BigInteger.ZERO : project.getStorageSize();
        BigInteger fileSize = BigInteger.valueOf(file.getSize());

        BigInteger maxSize = project.getMaxStorageSize() == null
                ? BigInteger.valueOf(minioProperties.getDefaultMaxSize())
                : project.getMaxStorageSize();
        if (currentSize.add(fileSize).compareTo(maxSize) > 0) {
            throw new IllegalArgumentException("Превышен лимит хранилища проекта");
        }

        String key = "project-" + projectId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        String uploadedKey;
        try {
            uploadedKey = minioService.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при получении потока файла", e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла в MinIO", e);
        }

        ResourceCreateDto dto = new ResourceCreateDto(
                file.getOriginalFilename(),
                uploadedKey,
                file.getSize(),
                file.getContentType(),
                teamMemberId,
                projectId
        );

        Resource resource = resourceMapper.toResource(dto);

        Resource savedResource = resourceRepository.save(resource);

        project.setStorageSize(currentSize.add(fileSize));
        projectRepository.save(project);

        log.info("Файл '{}' успешно загружен в проект '{}'", file.getOriginalFilename(), project.getName());
        return resourceResultMapper.toResultDto(savedResource);
    }

    public void deleteResource(Long resourceId, Long teamMemberId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Ресурс не найден"));

        TeamMember currentUser = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!resource.getCreatedBy().getId().equals(teamMemberId)
                && !currentUser.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("У пользователя нет прав для удаления данного файла");
        }

        if (resource.getKey() != null) {
            minioService.deleteFile(resource.getKey());
        }

        Project project = resource.getProject();
        BigInteger currentSize = project.getStorageSize() == null ? BigInteger.ZERO : project.getStorageSize();
        BigInteger fileSize = resource.getSize() == null ? BigInteger.ZERO : resource.getSize();
        if (currentSize.compareTo(fileSize) >= 0) {
            project.setStorageSize(currentSize.subtract(fileSize));
        } else {
            project.setStorageSize(BigInteger.ZERO);
        }
        projectRepository.save(project);

        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);

        TeamMember updater = new TeamMember();
        updater.setId(teamMemberId);
        resource.setUpdatedBy(updater);

        resourceRepository.save(resource);

        log.info("Ресурс с ID {} успешно удалён пользователем с ID {}", resourceId, teamMemberId);
    }
}
