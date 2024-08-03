package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3;
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ResourceRepository resourceRepository;
    private final UserContext userContext;
    private final ResourceValidator validator;
    private final ResourceMapper resourceMapper;
    private final ProjectRepository projectRepository;
private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamRepository teamRepository;

    private final String DEFAULT_BUCKET = "project";

    public ResourceDto add(MultipartFile file, List<TeamRole> allowedTeamRoles, Long projectId) {

        long userId = userContext.getUserId();

//        Project project1 = new Project();
//        project1.setName("name");
//        project1.setDescription("Desc");
//        project1.setOwnerId(2L);
//        project1.setStatus(ProjectStatus.CREATED);
//        project1.setVisibility(ProjectVisibility.PUBLIC);
//        project1.setMaxStorageSize(BigInteger.valueOf(2_147_483_648L));
//        project1.setStorageSize(BigInteger.ZERO);
//        project1 = projectRepository.save(project1);
//        Team team = new Team();
//        team.setProject(project1);
//        team = teamRepository.save(team);
//        TeamMember teamMember1 = new TeamMember();
//        teamMember1.setUserId(userId);
//        teamMember1.setRoles(List.of(TeamRole.OWNER, TeamRole.MANAGER, TeamRole.DEVELOPER));
//        teamMember1.setTeam(team);
//        teamMemberJpaRepository.save(teamMember1);




        Project project = projectRepository.getProjectById(projectId);

        validator.validateStorageOverflow(project, file);
        TeamMember teamMember = validator.getTeamMember(project, userId);


        //teamMember.setId(4L);
        if (allowedTeamRoles == null || allowedTeamRoles.isEmpty()) {
            allowedTeamRoles = new ArrayList<>(teamMember.getRoles());
        }
        LocalDateTime now = LocalDateTime.now();
        String key = file.getOriginalFilename() + now;
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(allowedTeamRoles);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);
        resource.setProject(project);
        String path = projectId + "/" + key;

        if (!s3.client.doesBucketExistV2(DEFAULT_BUCKET)) {
            s3.client.createBucket(DEFAULT_BUCKET);
        }

        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            s3.client.putObject(DEFAULT_BUCKET, path, inputStream, data);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        Resource result = resourceRepository.save(resource);
        ResourceDto resourceDto = resourceMapper.toDto(result);
        project.setStorageSize(project.getStorageSize().add(BigInteger.valueOf(file.getSize())));
        projectRepository.save(project);
        return resourceDto;
    }

    public ResponseEntity<InputStreamResource> get(Long resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        Long projectId = resource.getProject().getId();

        String path = projectId + "/" + resource.getKey();
        S3Object s3Object = s3.client.getObject(DEFAULT_BUCKET, path);
        InputStream inputStream = s3Object.getObjectContent();

        String mimeType = s3Object.getObjectMetadata().getContentType();
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }
        MediaType mediaType = MediaType.parseMediaType(mimeType);
        String encodedFileName = UriUtils.encode(resource.getName(), StandardCharsets.UTF_8);
        String contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        headers.setContentType(mediaType);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .contentType(mediaType)
                .body(new InputStreamResource(inputStream));

    }

}
