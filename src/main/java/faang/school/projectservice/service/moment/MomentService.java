package faang.school.projectservice.service.moment;

import faang.school.projectservice.validator.moment.ValidatorMoment;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ValidatorMoment validatorMoment;
    private final MomentMapper momentMapper;
    @Transactional
    public void createMoment(MomentDto momentDto){
        validatorMoment.ValidatorMomentName(momentDto);
        validatorMoment.ValidatorOpenProject(momentDto);
        validatorMoment.ValidatorMomentProject(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        momentRepository.save(moment);
    }

    public void updateMoment(MomentDto momentDto){
       // Moment moment = momentMapper.toEntity(momentDto);
            momentDto.getProjectIds() ==

                momentDto.getUserIds()
                        .distinct()
                        .collect(Collectors.toList());
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

    public void validatorMoment(Moment data, Project project){

    }
}
