package faang.school.projectservice.service.vacancy;

import org.springframework.web.multipart.MultipartFile;

public interface VacancyCoverService {
    void uploadVacancyCover(Long vacancyId, MultipartFile multipartFile);

    void deleteVacancyCover(Long vacancyId);
}
