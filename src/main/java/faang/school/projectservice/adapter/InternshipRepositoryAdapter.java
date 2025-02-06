package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InternshipRepositoryAdapter {
    private final InternshipRepository internshipRepository;

    public Internship save(Internship internship) {
        return internshipRepository.save(internship);
    }

    public Internship findById(Long id) {
        return internshipRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String
                        .format("Internship with id: %d not found!", id)));
    }

    public Stream<Internship> findAll() {
        return internshipRepository.findAll().stream();
    }

    public Stream<Internship> findAllByProjectId(Long projectId) {
        return internshipRepository.findAllByProjectId(projectId).stream();
    }
}
