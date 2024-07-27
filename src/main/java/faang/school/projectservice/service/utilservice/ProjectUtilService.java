package faang.school.projectservice.service.utilservice;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ProjectUtilService {

    private final ProjectJpaRepository projectJpaRepository;

    @Transactional(readOnly = true)
    public Project getById(long id) {
        return projectJpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Project id=%d not found", id))
                );
    }

    public Project save(Project project) {
        return projectJpaRepository.save(project);
    }
}
