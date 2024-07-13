package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    public InternshipDto create(InternshipDto internshipDto) {
        Internship oldInternship = internshipMapper.toEntity(internshipDto);
        Internship newInternship = internshipRepository.save(oldInternship);

        return internshipMapper.toDto(newInternship);
    }

    public InternshipDto updateEvent(InternshipDto internshipDto) {
        if (checkInternShipForUpdating(internshipDto)){

        }

            return internshipMapper.toDto(internshipRepository.save(internshipMapper.toEntity(internshipDto)));
        //return null;
    }

    private boolean checkInternShipForUpdating(InternshipDto internshipDto) {
        //началась ли стажировка
        if (internshipHasStarted(internshipDto)) {
            //Если стажировка завершена,
            if (internshipDto.getStatus().equals(InternshipStatus.COMPLETED)) {



              //  for(TeamMemberDto intern : internshipDto.getInterns())
                //   if(intern.)

               // if(internshipDto.getInterns().)
                //то стажирующиеся должны получить новые роли на проекте, если прошли
                // Участник считается прошедшим стажировку, если все запланированные задачи выполнены.
            } else {
                // и быть удалены из списка участников проекта, если не прошли.
            }



        }
        return internshipHasStarted(internshipDto)
                && internshipStatusIsCompleted(internshipDto);
    }

    private boolean checkInternShip(InternshipDto internshipDto) {
        return internshipStatusIsCompleted(internshipDto) &&
                haveAllTasksDone(internshipDto)
                && internshipHasStarted(internshipDto);
    }

    private boolean haveAllTasksDone(InternshipDto internshipDto) {
        //если все задачи выполнены
        return internshipDto.getProject().getTasks().stream()
                .allMatch(task ->
                        task.getStatus().equals(TaskStatus.DONE)
                        ||
                        task.getStatus().equals(TaskStatus.CANCELLED)
                );
    }

    private boolean internshipStatusIsCompleted(InternshipDto internshipDto) {
        //если стажировка завершена

        if(internshipDto.getStatus().equals(InternshipStatus.COMPLETED)
        && (haveAllTasksDone(internshipDto))){

            //2. Если стажировка завершена,
            // то стажирующиеся должны получить новые роли на проекте, если прошли
            Internship internship = internshipMapper.toEntity(internshipDto);



            internshipRepository.save(internship);
        }
        // и быть удалены из списка участников проекта, если не прошли.
        return internshipDto.getStatus().equals(InternshipStatus.COMPLETED);
    }

    private boolean internshipHasStarted(InternshipDto internshipDto) {
        //началась ли стажировка
        return !(internshipDto.getStartDate().isBefore(LocalDateTime.now()));
    }



}
