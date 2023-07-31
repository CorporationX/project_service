package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentDtoUpdate;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.filters.moments.MomentFilter;
import faang.school.projectservice.filters.moments.MomentMapper;
import faang.school.projectservice.filters.moments.MomentMapperImpl;
import faang.school.projectservice.filters.moments.filtersForFilterMomentDto.MomentDescriptionFilter;
import faang.school.projectservice.filters.moments.filtersForFilterMomentDto.MomentNameFilter;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    private FilterMomentDto filterMomentDto;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private MomentMapper momentMapper = new MomentMapperImpl();
    @Mock
    private List<MomentFilter> momentFilter;
    private Moment moment;
    @InjectMocks
    private MomentService momentService;

    @Mock
    private Project project;

    @BeforeEach
    void setUp() {
        MomentFilter momentNameFilter = new MomentNameFilter();
        momentFilter = List.of(momentNameFilter, new MomentDescriptionFilter());
        momentService = new MomentService(momentRepository, projectRepository, momentMapper, momentFilter);
        moment = new Moment();
        moment.setName("first important moment");
        moment.setDate(LocalDateTime.now());
        moment.setDescription("description");
        filterMomentDto = new FilterMomentDto();
        filterMomentDto.setNamePattern("first");
        filterMomentDto.setDescriptionPattern("desc");
        project = new Project();
        project.setStatus(ProjectStatus.CREATED);
        project.setId(100L);
        moment.setProject(List.of(project));
    }

    @Test
    void createMomentCallsRepositoryMethod() {
        MomentDto momentDto = new MomentDto("testDto", LocalDateTime.now(), 100L);
        Moment moment =  momentMapper.dtoToMoment(momentDto);
        Mockito.when(momentRepository.save(momentMapper.dtoToMoment(momentDto))).thenReturn(moment);
        Mockito.when(projectRepository.getProjectById(momentDto.getIdProject())).thenReturn(project);

        Moment createdMoment = momentService.createMoment(momentDto);

        Mockito.verify(momentRepository, Mockito.times(1))
                .save(momentMapper.dtoToMoment(momentDto));
        assertEquals(createdMoment.getName(), momentDto.getName());
        assertEquals(createdMoment.getDate(), momentDto.getDate());
    }

    @Test
    void updateMomentCallsRepositoryMethod() {
        MomentDtoUpdate momentDtoUpdate = new MomentDtoUpdate(25L, "new Name", LocalDateTime.now(), 100L);
        Moment updatedMoment =  momentMapper.dtoUpdatedToMoment(momentDtoUpdate);
        Moment deprecatedMoment = Moment.builder().id(25L).name("old Name").build();

        Mockito.when(momentRepository.save(momentMapper.dtoUpdatedToMoment(momentDtoUpdate))).thenReturn(updatedMoment);
        Mockito.when(momentRepository.findById(momentDtoUpdate.getId())).thenReturn(Optional.of(deprecatedMoment));
        Mockito.when(momentRepository.findById(updatedMoment.getId())).thenReturn(Optional.of(updatedMoment));

        momentService.updateMoment(momentDtoUpdate);
        Moment dbMoment = momentRepository.findById(updatedMoment.getId()).orElse(new Moment());

        Mockito.verify(momentRepository, Mockito.times(1))
                .save(momentMapper.dtoUpdatedToMoment(momentDtoUpdate));
        assertEquals(updatedMoment.getName(), dbMoment.getName());
        assertNotEquals(deprecatedMoment.getName(), dbMoment.getName());
    }

    @Test
    void getFilteredMomentsReturnsValidList() {
        Moment invalidMoment = Moment.builder().name("First").description("invalid").project(List.of(project)).build();
        Mockito.when(momentRepository.findAll()).thenReturn(List.of(moment, invalidMoment));

        assertEquals(1, momentService.getFilteredMoments(filterMomentDto, 100L).size());
        Mockito.verify(momentRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getAllMomentsCallsRepositoryMethod() {
        Mockito.when(momentRepository.findAll()).thenReturn(List.of(moment));

        List<MomentDtoUpdate> result = momentService.getAllMoments();

        Mockito.verify(momentRepository, Mockito.times(1))
                .findAll();
        assertEquals(List.of(momentMapper.momentToDtoUpdated(moment)), result);
    }

    @Test
    void getMomentCallsRepositoryMethod() {
        Optional<Moment> optionalMoment = Optional.of(moment);
        Mockito.when(momentRepository.findById(1L)).thenReturn(optionalMoment);

        momentService.getMoment(1L);

        Mockito.verify(momentRepository, Mockito.times(1))
                .findById(1L);
        assertEquals(momentRepository.findById(1L).get().getName(), optionalMoment.get().getName());
    }
}