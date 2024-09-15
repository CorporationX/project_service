package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.moment.MomentFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.validation.OverridesAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
    @Spy
    private MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    @Captor
    ArgumentCaptor<Moment> captorMoment;
    @Captor
    ArgumentCaptor<MomentDto> captorMomentDto;

    @BeforeEach
    public void setup() {
        momentDto = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        momentDtoToReturn = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        momentToReturn = new Moment();
        momentToReturn.setId(1L);
        momentToReturn.setName("Moment");
        momentToReturn.setDescription("description");
        momentToReturn.setDate(LocalDateTime.of(2024, 12, 31, 12, 0, 0));
        momentToReturn.setProjects( new ArrayList<>(
                List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(3L).build()
                )
        ));
        momentToReturn.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        momentToReturn.setImageId("imageId1");
        momentToReturn.setCreatedAt(momentDto.createdAt());
        momentToReturn.setUpdatedAt(momentDto.updatedAt());
        expectedMoment = new Moment();
        expectedMoment.setId(1L);
        expectedMoment.setName("Moment");
        expectedMoment.setDescription("description");
        expectedMoment.setDate(LocalDateTime.of(2024, 12, 31, 12, 0, 0));
        expectedMoment.setProjects( new ArrayList<>(
                List.of(
                        Project.builder().id(1L).build(),
                        Project.builder().id(2L).build(),
                        Project.builder().id(3L).build()
                )
        ));
        expectedMoment.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        expectedMoment.setImageId("imageId1");
        expectedMoment.setCreatedAt(momentDto.createdAt());
        expectedMoment.setUpdatedAt(momentDto.updatedAt());
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
        Assertions.assertEquals("Момент можно создать только для незакрытого проекта", exception.getMessage());
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
    public void testUpdate_create() {
        // Arrange
        when(momentRepository.findById(momentDto.id())).thenReturn(Optional.empty());
        when(service.create(momentDto)).thenReturn(momentDtoToReturn);

        // Act and Assert
        MomentDto returnMomentDto = service.update(momentDto);
        verify(service, times(1)).create(captorMomentDto.capture());
        Assertions.assertEquals(expectedMomentDto, captorMomentDto.capture());
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }

    @Test
    public void testUpdate_NotNewProjectAndTeamMember() {
        // Arrange
        momentToReturn.setDescription("newDescription");
        expectedMoment.setDescription("newDescription");
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "newDescription",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(momentRepository.findById(momentDto.id())).thenReturn(Optional.of(momentToReturn));


        // Act and Assert
        MomentDto returnMomentDto = service.update(momentDto);
        verify(momentRepository, times(1)).save(captorMoment.capture());
        Assertions.assertEquals(expectedMoment, captorMoment.getValue());
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }
    @Test
    public void testUpdate_NewProject() {
        // Arrange
        momentToReturn.setDescription("newDescription");
        momentToReturn.setProjects(new ArrayList<>(List.of(Project.builder().id(1L).build())));
        expectedMoment.setDescription("newDescription");
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "newDescription",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L, 10L, 20L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
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

        // Act and Assert
        MomentDto returnMomentDto = service.update(momentDto);
        verify(momentRepository, times(1)).save(captorMoment.capture());
        Assertions.assertEquals(expectedMoment, captorMoment.getValue());
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }

    @Test
    public void testUpdate_NewUsers() {
        // Arrange
        momentToReturn.setDescription("newDescription");
        momentToReturn.setUserIds(new ArrayList<>(List.of(1L)));
        expectedMoment.setDescription("newDescription");
        expectedMoment.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L, 10L, 20L)));
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "newDescription",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L, 10L, 20L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(momentRepository.findById(momentDto.id())).thenReturn(Optional.of(momentToReturn));
        when(projectRepository.findAllByIds(any())).thenReturn(new ArrayList<>());
        List<TeamMember> newTeamMembers = new ArrayList<>(
                List.of(
                        TeamMember.builder()
                                .team(
                                        Team.builder()
                                                .project(
                                                        Project.builder().id(10L).build()
                                                ).build()
                                )
                                .build(),
                        TeamMember.builder()
                                .team(
                                        Team.builder()
                                                .project(
                                                        Project.builder().id(20L).build()
                                                ).build()
                                )
                                .build()
                )
        );
        when(teamMemberRepository.findAllById(any())).thenReturn(newTeamMembers);

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
        momentOne.setUserIds(new ArrayList<>(List.of(1L, 2L, 3L)));
        Moment momentTwo = new Moment();
        momentTwo.setId(2L);
        momentTwo.setDate(LocalDateTime.of(2024, 8, 1, 1, 1, 1));
        momentTwo.setUserIds(new ArrayList<>(List.of(10L, 2L, 3L)));
        Moment momentThree = new Moment();
        momentThree.setId(3L);
        momentThree.setDate(LocalDateTime.of(2024, 9, 1, 1, 1, 1));
        momentThree.setUserIds(new ArrayList<>(List.of(1L, 2L, 33L)));
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
                                new ArrayList<>(List.of(1L, 2L, 3L)),
                                null,
                                null,
                                null,
                                null
                        ),
                        new MomentDto(
                                2L,
                                null,
                                null,
                                LocalDateTime.of(2024, 8, 1, 1, 1, 1),
                                new ArrayList<>(List.of(10L, 2L, 3L)),
                                null,
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

        // Act
        MomentDto returnDto = service.getMoment(id);

        // Assert
        Assertions.assertEquals(expectedMomentDto, returnDto);
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
