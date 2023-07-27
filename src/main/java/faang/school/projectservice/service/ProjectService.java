package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ResponseProjectDto getAllByFilter(ProjectFilterDto dto) {
    }

    public List<ResponseProjectDto> getAll() {
    }

    public ResponseProjectDto getById(Long id) {
    }
}
