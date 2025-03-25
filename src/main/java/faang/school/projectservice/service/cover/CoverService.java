package faang.school.projectservice.service.cover;

import org.springframework.web.multipart.MultipartFile;

public interface CoverService {
    String uploadCover(MultipartFile multipartFile, Long vacancyId);
}
