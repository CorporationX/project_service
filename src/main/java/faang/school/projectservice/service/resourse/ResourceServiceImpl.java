package faang.school.projectservice.service.resourse;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

/**
 * На подумать:
 * ToDo: Проверять уникальность файлов
 * ToDo: Множественные реализации через абстрактный класс
 * ToDo: Возвращать предупреждение, что файл с таким именем существует
 */
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    protected final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;

    @Override
    public Resource uploadEntityFile(MultipartFile file, long id) {
        Project project = projectRepository.findById(id).orElseThrow();
        validateFreeSpace(project.getStorageSize(), project.getMaxStorageSize(), file.getSize());

        /**
         * ToDo: проверить, что пользователь добавляющий файл является участником команды
         */
        teamMemberRepository.findByUserId(userContext.getUserId());


        Resource uploadedResource = s3Service.uploadFile(file, project.getName());

//        isTeamMember = Stream.ofNullable(project.getTeams())
//                  .flatMap(List::stream)
//                .flatMap(team -> team.getTeamMembers().stream())
//                .anyMatch(teamMember -> teamMember.getId() == userId);

        /**
         * ToDo: Установить создателя и остальные нужные поля, сохранить файл, обновить дату проекта
         */

//        uploadedResource.setCreatedBy();
//        resourceRepository.save(Resource);

        return null;
    }

    @Override
    public URL getFileUrl(String fileKey) {
        return s3Service.getFileUrl(fileKey);
    }

    @Override
    public void deleteFile(String fileKey) {
        s3Service.deleteFile(fileKey);
    }

    private void validateFreeSpace(BigInteger currentSize, BigInteger maxSize, long fileSize) {
        int freeSpace = maxSize.getLowestSetBit() - currentSize.getLowestSetBit();
        if (freeSpace > fileSize) {
            return;
        }

        throw new DataValidationException(String.format("Can't upload file. Storage size limit reached. File size: %d. Free space: %d",
                fileSize, freeSpace));
    }

    public void validateTeamMember() {

    }
}
