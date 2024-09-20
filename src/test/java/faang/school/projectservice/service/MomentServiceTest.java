package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.filter.moment.MomentFilterMonth;
import faang.school.projectservice.filter.moment.MomentFilterProjects;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    private MomentDto momentDto;
    private MomentDto expectedMomentDto;
    private MomentDto momentDtoToReturn;
    private Moment momentToReturn;
    private Moment expectedMoment;
    @InjectMocks
    private MomentService service;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberRepository;
    private List<MomentFilter> momentFilters;
    private MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    @Captor
    ArgumentCaptor<Moment> captorMoment;
    @Captor
    ArgumentCaptor<MomentDto> captorMomentDto;

    MomentDto createMomentDto(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                "imageId1",
                createdAt,
                updatedAt
        );
    }

    Moment createMoment() {
        Moment moment = new Moment();
        moment.setId(1L);
        moment.setName("Moment");
        moment.setDescription("description");
        moment.setDate(LocalDateTime.of(2024, 12, 31, 12, 0, 0));
        moment.setProjects( new ArrayList<>(
                List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(3L).build()
                )
        ));
        moment.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        moment.setImageId("imageId1");
        moment.setCreatedAt(momentDto.createdAt());
        moment.setUpdatedAt(momentDto.updatedAt());
        return moment;
    }

    @BeforeEach
    public void setup() {
        momentFilters = new ArrayList<>(
                List.of(
                        new MomentFilterMonth(),
                        new MomentFilterProjects()
                )
        );
        service = new MomentService(
                projectRepository,
                momentRepository,
                teamMemberRepository,
                mapper,
                momentFilters
        );
        momentDto = createMomentDto(LocalDateTime.now(), LocalDateTime.now());
        expectedMomentDto = createMomentDto(momentDto.createdAt(), momentDto.updatedAt());
        momentDtoToReturn = createMomentDto(momentDto.createdAt(), momentDto.updatedAt());
        momentToReturn = createMoment();
        expectedMoment = createMoment();
    }

    @Test
    public void testCreate_ProjectClosed() {
        // Arrange
        List<Project> returnProjects = new ArrayList<>(
                List.of(
                        Project.builder().id(1L).status(ProjectStatus.CREATED).build(),
                        Project.builder().id(2L).status(ProjectStatus.CANCELLED).build(),
                        Project.builder().id(3L).status(ProjectStatus.COMPLETED).build()
                )
        );
        when(projectRepository.findAllByIds(momentDto.projectIds())).thenReturn(returnProjects);

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class,() -> service.create(momentDto));
        Assertions.assertEquals("Момент можно создать только для незакрытого проекта\n"
                +"закрытый проект с id = 3", exception.getMessage());
    }

    @Test
    public void testCreate() {
        // Arrange
        List<Project> returnProjects = new ArrayList<>(
                List.of(
                        Project.builder().id(1L).status(ProjectStatus.CREATED).build(),
                        Project.builder().id(2L).status(ProjectStatus.CREATED).build(),
                        Project.builder().id(3L).status(ProjectStatus.CREATED).build()
                )
        );
        when(projectRepository.findAllByIds(momentDto.projectIds())).thenReturn(returnProjects);
        when(momentRepository.save(any())).thenReturn(momentToReturn);

        // Act and Assert
        MomentDto returnMomentDto = service.create(momentDto);
        verify(momentRepository, times(1)).save(captorMoment.capture());
        Assertions.assertEquals(expectedMoment, captorMoment.getValue());
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }

    @Test
    public void testUpdate_momentNotExist() {
        // Arrange
        when(momentRepository.findById(momentDto.id())).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> service.update(momentDto));
        Assertions.assertEquals("Переданного момента с id = " + momentDto.id() + " не существует в бд", exception.getMessage());
    }

    @Test
    public void testUpdate_NotNewProjectAndTeamMember() {
        // Arrange
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                "imageId1",
                momentDto.createdAt(),
                momentDto.updatedAt()
        );
        when(momentRepository.findById(momentDto.id())).thenReturn(Optional.of(momentToReturn));
        when(momentRepository.save(any())).thenReturn(momentToReturn);

        // Act and Assert
        MomentDto returnMomentDto = service.update(momentDto);
        verify(momentRepository, times(1)).save(captorMoment.capture());
        Assertions.assertEquals(expectedMoment, captorMoment.getValue());
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }
    @Test
    public void testUpdate_NewProject() {
        // Arrange
        momentToReturn.setProjects(new ArrayList<>(List.of(Project.builder().id(1L).build())));
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "newDescription",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L, 10L, 20L)),
                "imageId1",
                momentToReturn.getCreatedAt(),
                momentToReturn.getUpdatedAt()
        );
        when(momentRepository.findById(momentDto.id())).thenReturn(Optional.of(momentToReturn));
        List<Project> newProject = new ArrayList<>(
                List.of(
                        Project.builder()
                                .id(2L)
                                .teams(new ArrayList<>(
                                        List.of(
                                                Team.builder()
                                                .teamMembers(
                                                        new ArrayList<>(
                                                                List.of(
                                                                        TeamMember.builder().id(10L).build(),
                                                                        TeamMember.builder().id(20L).build()
                                                                )
                                                        )
                                                )
                                                .build()
                                        )
                                ))
                                .build()
                )
        );
        when(projectRepository.findAllByIds(any())).thenReturn(newProject);
        expectedMoment.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L, 10L, 20L)));
        Moment momentToReturn =  new Moment(

        );
        momentToReturn = new Moment();
        momentToReturn.setId(1L);
        momentToReturn.setName("Moment");
        momentToReturn.setDescription("newDescription");
        momentToReturn.setDate(LocalDateTime.of(2024, 12, 31, 12, 0, 0));
        momentToReturn.setProjects( new ArrayList<>(
                List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(3L).build()
                )
        ));
        momentToReturn.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L, 10L, 20L)));
        momentToReturn.setImageId("imageId1");
        momentToReturn.setCreatedAt(momentDto.createdAt());
        momentToReturn.setUpdatedAt(momentDto.updatedAt());
        when(momentRepository.save(any())).thenReturn(momentToReturn);

        // Act and Assert
        MomentDto returnMomentDto = service.update(momentDto);
        verify(momentRepository, times(1)).save(captorMoment.capture());
        Assertions.assertEquals(expectedMoment, captorMoment.getValue());
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }

    @Test
    public void testUpdate_NewUsers() {
        // Arrange
        momentToReturn.setUserIds(new ArrayList<>(List.of(1L)));
        expectedMoment.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        expectedMoment.setProjects(new ArrayList<>(
                List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(3L).build(),
                        Project.builder().id(10L).build(),
                        Project.builder().id(20L).build()
                )
        ));
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L, 10L, 20L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                "imageId1",
                momentToReturn.getCreatedAt(),
                momentToReturn.getUpdatedAt()
        );
        when(momentRepository.findById(momentDto.id())).thenReturn(Optional.of(momentToReturn));
        when(projectRepository.findAllByIds(any())).thenReturn(new ArrayList<>());
        TeamMember teamMemberOne = TeamMember.builder()
                .team(
                        Team.builder()
                                .project(
                                        Project.builder().id(10L).build()
                                ).build()
                )
                .build();
        TeamMember teamMemberTwo = TeamMember.builder()
                .team(
                        Team.builder()
                                .project(
                                        Project.builder().id(20L).build()
                                ).build()
                )
                .build();
        List<TeamMember> newTeamMembers = new ArrayList<>(
                List.of(teamMemberOne, teamMemberTwo)
        );
        when(teamMemberRepository.findAllById(any())).thenReturn(newTeamMembers);
        when(momentRepository.save(any())).thenReturn(expectedMoment);

        // Act and Assert
        MomentDto returnMomentDto = service.update(momentDto);
        verify(momentRepository, times(1)).save(captorMoment.capture());
        Assertions.assertEquals(expectedMoment, captorMoment.getValue());
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }

    @Test
    public void testGetMoments() {
        // Arrange
        MomentFilterDto filterDto = new MomentFilterDto(Month.AUGUST, new ArrayList<>(List.of(1L, 2L)));
        Moment momentOne = new Moment();
        momentOne.setId(1L);
        momentOne.setDate(LocalDateTime.of(2024, 8, 1, 1, 1, 1));
        momentOne.setProjects(new ArrayList<>(
                List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(3L).build()
                )
        ));
        Moment momentTwo = new Moment();
        momentTwo.setId(2L);
        momentTwo.setDate(LocalDateTime.of(2024, 8, 1, 1, 1, 1));
        momentTwo.setProjects(new ArrayList<>(
                List.of(
                        Project.builder().id(10L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(3L).build()
                )
        ));
        Moment momentThree = new Moment();
        momentThree.setId(3L);
        momentThree.setDate(LocalDateTime.of(2024, 9, 1, 1, 1, 1));
        momentThree.setProjects(new ArrayList<>(
                List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(33L).build()
                )
        ));
        List<Moment> moments = new ArrayList<>(
                List.of(
                        momentOne, momentTwo, momentThree
                )
        );
        when(momentRepository.findAll()).thenReturn(moments);
        MomentDto expectedMomentDto = new MomentDto(
                1L,
                null,
                null,
                LocalDateTime.of(2024, 8, 1, 1, 1, 1),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                null,
                null,
                null,
                null
        );
        List<MomentDto> expectedMoments = new ArrayList<>(List.of(expectedMomentDto));
        // Act
        List<MomentDto> returnMoments = service.getMoments(filterDto);

        // Assert
        Assertions.assertEquals(expectedMoments, returnMoments);
    }

    @Test
    public void testGetAllMoments() {
        // Arrange
        Moment momentOne = new Moment();
        momentOne.setId(1L);
        momentOne.setDate(LocalDateTime.of(2024, 8, 1, 1, 1, 1));
        momentOne.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        Moment momentTwo = new Moment();
        momentTwo.setId(2L);
        momentTwo.setDate(LocalDateTime.of(2024, 8, 1, 1, 1, 1));
        momentTwo.setUserIds(new ArrayList<>(List.of(10L, 2L, 3L)));
        List<Moment> moments = new ArrayList<>(
                List.of(
                        momentOne, momentTwo
                )
        );
        when(momentRepository.findAll()).thenReturn(moments);
        List<MomentDto> expectedDtos = new ArrayList<>(
                List.of(
                        new MomentDto(
                                1L,
                                null,
                                null,
                                LocalDateTime.of(2024, 8, 1, 1, 1, 1),
                                null,
                                new ArrayList<>(List.of(1L, 2L, 3L)),
                                null,
                                null,
                                null
                        ),
                        new MomentDto(
                                2L,
                                null,
                                null,
                                LocalDateTime.of(2024, 8, 1, 1, 1, 1),
                                null,
                                new ArrayList<>(List.of(10L, 2L, 3L)),
                                null,
                                null,
                                null
                        )
                )
        );

        // Act
        List<MomentDto> returnDtos = service.getAllMoments();

        // Assert
        Assertions.assertEquals(expectedDtos, returnDtos);

    }

    @Test
    public void testGetMoment_null() {
        // Arrange
        long id = 1;
        when(momentRepository.findById(id)).thenReturn(Optional.empty());
        expectedMomentDto = null;

        // Act and Assert
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> service.getMoment(id));
        Assertions.assertEquals("Момент с id = " + id + " не найден в системе", exception.getMessage());
    }

    @Test
    public void testGetMoment() {
        // Arrange
        long id = 1;
        when(momentRepository.findById(id)).thenReturn(Optional.of(momentToReturn));

        // Act
        MomentDto returnDto = service.getMoment(id);

        // Assert
        Assertions.assertEquals(expectedMomentDto, returnDto);
    }
}
