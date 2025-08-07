package faang.school.projectservice.service.presentation;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.presentation.ProjectPresentationDto;
import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.dto.project.stats.ProjectStatsDto;
import faang.school.projectservice.dto.resource.S3FileResponse;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exeption.S3DownloadException;
import faang.school.projectservice.mapper.ProjectInfoMapper;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.presentation.generator.PdfGenerator;
import faang.school.projectservice.service.presentation.stats.ProjectPresentationStatsServiceImpl;
import faang.school.projectservice.service.presentation.uploader.UploaderService;
import faang.school.projectservice.service.s3.FileKeyGenerator;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectPresentationServiceImpl implements ProjectPresentationService {

    private final ProjectService projectService;
    private final ProjectInfoMapper infoMapper;
    private final TeamMapper teamMapper;
    private final TaskMapper taskMapper;
    private final ProjectPresentationStatsServiceImpl statsService;
    private final PdfGenerator pdfGenerator;
    private final FileKeyGenerator fileKeyGenerator;
    private final S3Service s3Service;
    private final UploaderService uploaderService;
    private final UserService userService;

    public static final String PDF_FILE_NAME = "presentation.pdf";
    private static final String CONTENT_TYPE_PDF = "application/pdf";

    @Override
    public void create(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        UserDto owner = userService.getById(project.getOwnerId());
        ProjectPresentationDto dto = buildPresentationDto(project, owner.username());

        File pdfFile = pdfGenerator.generateToFile(dto);
        String fileKey = fileKeyGenerator.generateForProject(projectId);

        try {
            uploaderService.uploadPdf(project, pdfFile, fileKey, CONTENT_TYPE_PDF);
            project.setPresentationFileKey(fileKey);
            project.setPresentationGeneratedAt(LocalDateTime.now());
            projectService.save(project);
        } finally {
            deleteTempFile(pdfFile);
        }
    }

    private void deleteTempFile(File file) {
        if (file != null && file.exists() && !file.delete()) {
            file.deleteOnExit();
        }
    }

    private ProjectPresentationDto buildPresentationDto(Project project, String username) {
        ProjectInfoDto infoDto = infoMapper.toProjectInfoDto(project, username);
        List<TeamDto> teamDtos = teamMapper.toDtoList(project.getTeams());
        List<TaskDto> tasks = taskMapper.toDtoList(project.getTasks());
        ProjectStatsDto stats = statsService.calculate(project);
        return new ProjectPresentationDto(infoDto, teamDtos, tasks, stats);
    }

    @Override
    public S3FileResponse downloadPresentation(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        String key = project.getPresentationFileKey();
        if (key == null) {
            log.warn("Presentation for project {} has not been generated", projectId);
            throw new S3DownloadException("Presentation not generated");
        }
        InputStream stream = s3Service.download(key);
        return new S3FileResponse(PDF_FILE_NAME, new InputStreamResource(stream), "pdf");
    }
}
