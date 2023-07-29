package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    private static final long INTERNSHIP_ID = 1L;
    private static final long MENTOR_ID = 2L;
    private static final long PROJECT_ID = 3L;
    private static final long INTERN_ID_1 = 13000L;
    private static final long INTERN_ID_2 = 34023L;
    private static final long INTERN_ID_3 = 31112L;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2023, 1, 1, 15, 30);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2023, 1, 1, 15, 30);

    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipMapper internshipMapper;


    @Test
    void getAllInternshipsTest() {
        when(internshipRepository.findAll())
            .thenReturn(Collections.singletonList(getInternship()));

        when(internshipMapper.toListDto(Collections.singletonList(getInternship())))
            .thenReturn(Collections.singletonList(getInternshipDto()));

        List<InternshipDto> internshipDtoList = internshipService.getAllInternships();

        verify(internshipRepository).findAll();
        verify(internshipMapper).toListDto(Collections.singletonList(getInternship()));
        assertEquals(internshipDtoList, List.of(getInternshipDto()));
    }

    @Test
    void getInternshipByTest() {
        when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(getInternship()));

        when(internshipMapper.toDto(getInternship())).thenReturn(getInternshipDto());

        InternshipDto internshipDto = internshipService.getInternshipBy(INTERNSHIP_ID);

        verify(internshipRepository).findById(INTERNSHIP_ID);
        verify(internshipMapper).toDto(getInternship());
        assertEquals(internshipDto, getInternshipDto());
    }

    private Internship getInternship() {
        Internship internship = new Internship();
        internship.setId(INTERNSHIP_ID);
        internship.setProject(Project.builder().id(PROJECT_ID).build());
        internship.setMentorId(TeamMember.builder().id(MENTOR_ID).build());
        internship.setInterns(List.of(
            TeamMember.builder().id(INTERN_ID_1).build(),
            TeamMember.builder().id(INTERN_ID_2).build(),
            TeamMember.builder().id(INTERN_ID_3).build()
        ));
        internship.setStartDate(START_DATE);
        internship.setEndDate(END_DATE);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        return internship;
    }

    private InternshipDto getInternshipDto() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(INTERNSHIP_ID);
        internshipDto.setProjectId(PROJECT_ID);
        internshipDto.setMentorId(MENTOR_ID);
        internshipDto.setInternIds(List.of(INTERN_ID_1, INTERN_ID_2, INTERN_ID_3));
        internshipDto.setStartDate(START_DATE);
        internshipDto.setEndDate(END_DATE);
        internshipDto.setStatus(InternshipStatus.IN_PROGRESS);
        return internshipDto;
    }
}