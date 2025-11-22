package faang.school.projectservice.service;

import faang.school.projectservice.dto.FileDownloadResponse;
import faang.school.projectservice.dto.ResourceDTO;
import faang.school.projectservice.enums.Role;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.exception.FileStorageException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.exception.StorageLimitExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class FileStorageService {
    private static final long MAX_FILE_SIZE = 500_000_000L; // 500MB max per file
    private static final long BYTES_PER_MB = 1_000_000L;
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of("exe", "bat", "cmd", "sh");

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final Tika tika = new Tika();

    public Resource uploadFile(MultipartFile file, Long projectId, Long teamMemberId, Set<Role> allowedRoles) {

        log.info("Uploading file {} to project {}", file.getOriginalFilename(), projectId);

        if (file.isEmpty()) {
            throw new FileStorageException("File cannot be empty");
        }

        validateFile(file);

        if (allowedRoles == null) {
            allowedRoles = Set.of();
        }

        Project project = projectRepository.findByIdWithLock(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        validateStorageLimit(project, file.getSize());

        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(()  -> new ResourceNotFoundException("Team member not found"));

        List<TeamRole> teamRoleList;
        if (allowedRoles == null || allowedRoles.isEmpty()) {
            teamRoleList = new ArrayList<>(teamMember.getRoles());
        } else {
            teamRoleList = convertRolesToTeamRoles(allowedRoles);
        }

        try {
            String key = generateStorageKey(projectId, file.getOriginalFilename());

            uploadToMinio(file, key);

            String contentType = detectContentType(file);
            Resource resource = Resource.builder()
                    .name(file.getOriginalFilename())
                    .key(key)
                    .size(BigInteger.valueOf(file.getSize()))
                    .contentType(contentType)
                    .type(ResourceType.getResourceType(contentType))
                    .status(ResourceStatus.ACTIVE)
                    .allowedRoles(teamRoleList)
                    .project(project)
                    .createdBy(teamMember)
                    .updatedBy(teamMember)
                    .build();

            resource = resourceRepository.save(resource);

            updateProjectStorageSize(project.getId());

            log.info("File uploaded successfully: {}", key);
            return resource;

        } catch (Exception e) {
            log.error("Error uploading file {} to project {}", file.getOriginalFilename(), projectId, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public FileDownloadResponse downloadFile(Long resourceId, Long projectId, Long teamMemberId) {
        log.info("Downloading resource {} from project {} for member {}", resourceId, projectId, teamMemberId);

        Resource resource = resourceRepository.findByIdAndProjectId(resourceId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found or doesn't belong to project"));

        validateAccess(resource, teamMemberId);

        if (resource.getStatus() != ResourceStatus.ACTIVE) {
            throw new FileStorageException("Resource is not active");
        }

        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(resource.getKey())
                            .build()
            );

            return FileDownloadResponse.builder()
                    .fileName(resource.getName())
                    .size(resource.getSize() != null ? resource.getSize().longValue() : null)
                    .contentType(resource.getContentType())
                    .inputStream(response)
                    .build();

        } catch (Exception e) {
            log.error("Failed to download file", e);
            throw new FileStorageException("Failed to download file", e);
        }
    }

    @Transactional
    public void deleteFile(Long resourceId, Long projectId, Long teamMemberId) {
        log.info("Deleting resource {} from project {} by member {}", resourceId, projectId, teamMemberId);

        Resource resource = resourceRepository.findByIdAndProjectId(resourceId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found or doesn't belong to project"));

        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found"));

        validateDeletePermission(resource, teamMember);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            log.warn("Resource {} is already deleted", resourceId);
            return;
        }

        try {
            if (resource.getKey() != null) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(resource.getKey())
                                .build()
                );
                log.info("File removed from MinIO: {}", resource.getKey());
            }

            resource.setKey(null);
            resource.setSize(BigInteger.ZERO);
            resource.setStatus(ResourceStatus.DELETED);
            resource.setUpdatedBy(teamMember);
            resourceRepository.save(resource);

            updateProjectStorageSize(resource.getProject().getId());

            log.info("Resource {} deleted successfully", resourceId);

        } catch (Exception e) {
            log.error("Failed to delete file", e);
            throw new FileStorageException("Failed to delete file", e);
        }
    }

    public Page<ResourceDTO> getProjectFiles(Long projectId, Long teamMemberId, Pageable pageable) {
        TeamMember teamMember = teamMemberRepository
                .findByIdAndProjectId(teamMemberId, projectId)
                .orElseThrow(() -> new FileStorageException("Not a project member"));

        Page<Resource> resources = resourceRepository
                .findByProjectIdAndStatus(projectId, ResourceStatus.ACTIVE, pageable);

        return resources.map(this::toDTO);
    }

    public String generatePresignedUrl(Long resourceId, Long projectId, Long teamMemberId) {
        Resource resource = resourceRepository.findByIdAndProjectId(resourceId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found or doesn't belong to project"));

        validateAccess(resource, teamMemberId);

        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(resource.getKey())
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );

            log.info("Generated presigned URL for resource {}", resourceId);
            return url;

        } catch (Exception e) {
            log.error("Failed to generate presigned URL", e);
            throw new FileStorageException("Failed to generate download URL", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            long maxSizeMb = MAX_FILE_SIZE / BYTES_PER_MB;
            throw new FileStorageException(
                    String.format("File size exceeds maximum allowed size of %d MB",
                            maxSizeMb));
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (BLOCKED_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("File type is not allowed: " + extension);
        }
    }

    private void validateStorageLimit(Project project, long fileSize) {
        BigInteger currentSize = project.getStorageSize() != null
                ? project.getStorageSize()
                : BigInteger.ZERO;
        BigInteger newSize = currentSize.add(BigInteger.valueOf(fileSize));
        BigInteger maxSize = project.getMaxStorageSize() != null
                ? project.getMaxStorageSize()
                : BigInteger.ZERO;

        if (newSize.compareTo(maxSize) > 0) {
            long currentSizeMb = currentSize.longValue() / BYTES_PER_MB;
            long maxSizeMb = maxSize.longValue() / BYTES_PER_MB;
            throw new StorageLimitExceededException(
                    String.format("Storage limit exceeded. Current: %d MB, Limit: %d MB",
                            currentSizeMb, maxSizeMb));
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private String generateStorageKey(Long projectId, String fileName) {
        String timestamp = Instant.now().toEpochMilli() + "";
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedFileName = sanitizeFileName(fileName);

        return String.format("project-%d/%s-%s-%s",
                projectId, timestamp, uuid, sanitizedFileName);
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_")
                .replaceAll("_{2,}", "_")
                .toLowerCase();
    }

    private String detectContentType(MultipartFile file) {
        try {
            String detected = tika.detect(file.getInputStream());
            if (detected != null && !detected.isBlank()) {
                return detected;
            }
            return "application/octet-stream";
        } catch (Exception e) {
            return file.getContentType() != null
                    ? file.getContentType() : "application/octet-stream";
        }
    }


    private void uploadToMinio(MultipartFile file, String key) throws Exception {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(bucketName)
                .object(key)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
    }

    private void updateProjectStorageSize(Long projectId) {
        Long totalSize = resourceRepository.calculateProjectStorageSize(projectId);
        totalSize = totalSize != null ? totalSize : 0L;
        projectRepository.updateStorageSize(projectId, totalSize);
        log.debug("Updated project {} storage size to {}", projectId, totalSize);
    }

    private void validateAccess(Resource resource, Long teamMemberId) {
        TeamMember teamMember = teamMemberRepository
                .findByIdAndProjectId(teamMemberId, resource.getProject().getId())
                .orElseThrow(() -> new FileStorageException("Not a project member"));

        if (resource.getAllowedRoles() == null || resource.getAllowedRoles().isEmpty()) {
            throw new FileStorageException("Resource has no allowed roles configured");
        }

        boolean hasAccess = teamMember.getRoles().stream()
                .anyMatch(resource.getAllowedRoles()::contains);

        if (!hasAccess) {
            throw new FileStorageException("No permission to access this resource");
        }
    }

    private void validateDeletePermission(Resource resource, TeamMember teamMember) {
        boolean isCreator = resource.getCreatedBy().getId().equals(teamMember.getId());
        boolean isManager = teamMember.getRoles().contains(TeamRole.MANAGER);
        boolean canDelete = isCreator || isManager;

        if (!canDelete) {
            throw new FileStorageException(
                    "Only file creator or project manager can delete files");
        }
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private ResourceDTO toDTO(Resource resource) {
        Long size = resource.getSize() != null
                ? resource.getSize().longValue()
                : null;

        return ResourceDTO.builder()
                .id(resource.getId())
                .name(resource.getName())
                .size(size)
                .type(resource.getType())
                .contentType(resource.getContentType())
                .createdBy(resource.getCreatedBy().getNickname())
                .createdAt(resource.getCreatedAt())
                .build();
    }

    private List<TeamRole> convertRolesToTeamRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> {
                    try {
                        return TeamRole.valueOf(role.name());
                    } catch (IllegalArgumentException e) {
                        log.warn("Role {} cannot be converted to TeamRole, skipping", role);
                        return null;
                    }
                })
                .filter(teamRole -> teamRole != null)
                .collect(Collectors.toList());
    }

}
