package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.moment.ValidatorMoment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public static class MomentService {
    private final MomentRepository momentRepository;
    private final ValidatorMoment validatorMoment;
    private final MomentMapper momentMapper;
    private final Moment moment;

    @Transactional
    public void createMoment(MomentDto momentDto) {
        validatorMoment.ValidatorMomentName(momentDto);
        validatorMoment.ValidatorOpenProject(momentDto);
        validatorMoment.ValidatorMomentProject(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        momentRepository.save(moment);
    }

    public void updateMoment(MomentDto momentDto) {
        List<Long> oldProjectIds = moment.getProjects().stream()
                .map(Project::getId)
                .toList();
        List<Long> newProjectIds = momentDto.getProjectIds()
                .stream()
                .toList();
        if (oldProjectIds.equals(newProjectIds)) {
            System.out.println("There are no new projects");
        } else {
            moment.setUserIds(momentDto.getUserIds().stream()
                    .distinct()
                    .toList());
            //если пришла дто с новым айди приекта , то в момента обновляю айдишники юзеров
            momentRepository.save(moment);
            List<Long> oldUserIds = moment.getUserIds()
                    .stream()
                    .toList();
            List<Long> newUserIds = momentDto.getUserIds()
                    .stream()
                    .toList();
            if (oldUserIds.equals(newUserIds)) {
                System.out.println("No new members");
            } else {
                moment.setProjects();
            }
        }
    }


}
//        Project projectAdd = projectRepository.getProjectById();
//        if (!momentDto.getProjects().contains(projectAdd)) {
//            moment.getUserIds().add(userIds);
//        }
//       if (!moment.getUserIds().contains(userIds)) {
//            moment.getProjects().add(projectAdd);
//        }
//        momentRepository.save(moment);
//    }

public void validatorMoment(Moment data, Project project) {

}
}
