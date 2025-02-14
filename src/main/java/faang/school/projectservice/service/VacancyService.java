package faang.school.projectservice.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface VacancyService {
    void addCover(Long id, MultipartFile file);

    InputStream getVacancyCover(Long id);

    void deleteVacancyCover(@PathVariable Long id, Long userId);
}
