package faang.school.projectservice.validation.stage;

import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageValidatorImpl implements StageValidator {

    private final StageRepository stageRepository;

    @Override
    public void validateExistence(long id) {
        if (!stageRepository.existsById(id)) {
            throw new NotFoundException("Stage with id=" + id + " does not exist");
        }
    }
}
