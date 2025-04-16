package faang.school.projectservice.service.s3;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.dto.FileDto;

public interface S3Service {
    Resource uploadFile(FileDto file);
}
