package faang.school.projectservice.service;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccessControlService {

    private final VacancyRepository vacancyRepository;

    public void checkAccess(long vacancyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();
        Optional<Vacancy> vacancyOptional = vacancyRepository.findById(vacancyId);
        if (vacancyOptional.isPresent()) {
            Vacancy vacancy = vacancyOptional.get();
            if (!currentUserId.equals(String.valueOf(vacancy.getCreatedBy()))) {
                throw new RuntimeException("Access denied");
            }
        } else throw new RuntimeException("Vacancy not found");
    }
}
