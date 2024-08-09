package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public void addMomentByName(Project project, String momentName) {
        Optional<Moment> moment = findMomentByName(momentName);
        ProjectDto projectDto = projectMapper.toDto(project);
        if (moment.isEmpty()) {
            MomentDto momentDto = MomentDto.builder()
                    .name(momentName)
                    .build();
            createMoment(projectDto, momentDto);
        } else {
            updateMoment(moment.get(), projectDto);
        }
    }

    public void updateMoment(Moment moment, ProjectDto projectDto) {
    }

    public void createMoment(ProjectDto projectDto, MomentDto momentDto) {
    }

    public Optional<Moment> findMomentByName(String name) {
        Moment moment = new Moment();
        moment.setName(name);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "description", "date")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase("name");
        Example<Moment> momentExample = Example.of(moment, matcher);
        return momentRepository.findOne(momentExample);
    }
}
