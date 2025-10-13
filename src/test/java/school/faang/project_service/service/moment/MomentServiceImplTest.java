package school.faang.project_service.service.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.SearchMomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MomentServiceImplTest {

    @InjectMocks
    private MomentServiceImpl momentServiceImpl;

    @Mock
    private MomentRepository momentRepository;

    @Spy
    private MomentMapper momentMapper = Mappers.getMapper(MomentMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private List<MomentFilter> momentFilters;

    @Captor
    private ArgumentCaptor<Moment> captorMoment;

    @Test
    public void createMomentWithNullOrEmptyField() {
        CreateMomentDto momentDto = new CreateMomentDto(
                "Name",
                "description",
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                List.of(1L, 2L, 3L),
                List.of(100L, 101L, 102L),
                "",
                LocalDateTime.of(2026, Month.JANUARY, 4, 12,0),
                100L
        );
        assertThrows(DataValidationException.class, () -> momentServiceImpl.createMoment(momentDto));
    }

    @Test
    public void createMomentCreatesMoment() throws Exception {
        CreateMomentDto momentDto = new CreateMomentDto(
                "Name",
                "description",
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                List.of(1L, 2L, 3L),
                List.of(100L, 101L, 102L),
                "imageId",
                LocalDateTime.of(2026, Month.JANUARY, 4, 12,0),
                100L
        );

        momentServiceImpl.createMoment(momentDto);

        verify(momentRepository, times(1)).save(captorMoment.capture());
        Moment moment = captorMoment.getValue();

        assertEquals(momentDto.name(), moment.getName());
    }

    @Test
    public void updateMomentWithNullOrEmptyField() {
        UpdateMomentDto momentDto = new UpdateMomentDto(
                "",
                "description",
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                List.of(1L, 2L, 3L),
                List.of(100L, 101L, 102L),
                LocalDateTime.of(2026, Month.JANUARY, 4, 12,0),
                100L
        );
        long momentId = 1;
        assertThrows(DataValidationException.class, () -> momentServiceImpl.updateMoment(momentId, momentDto));
    }

    @Test
    public void updateMomentNonexistentMoment() {
        UpdateMomentDto momentDto = new UpdateMomentDto(
                "name",
                "description",
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                List.of(1L, 2L, 3L),
                List.of(100L, 101L, 102L),
                LocalDateTime.of(2026, Month.JANUARY, 4, 12,0),
                100L
        );
        long momentId = 1;
        assertThrows(EntityNotFoundException.class, () -> momentServiceImpl.updateMoment(momentId, momentDto));
    }

    @Test
    public void updateMomentUpdatesMoment() throws Exception {
        UpdateMomentDto momentDto = new UpdateMomentDto(
                "name",
                "description",
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                List.of(1L, 2L, 3L),
                List.of(100L, 101L, 102L),
                LocalDateTime.of(2026, Month.JANUARY, 4, 12,0),
                100L
        );
        Moment moment = new Moment();
        moment.setId(1L);
        long momentId = 1;

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));

        momentServiceImpl.updateMoment(momentId, momentDto);

        verify(momentRepository, times(1)).save(captorMoment.capture());
        Moment updatedMoment = captorMoment.getValue();

        assertEquals(momentId, updatedMoment.getId());
    }

    @Test
    public void getByIdNonexistentMoment() {
        long momentId = 1;
        assertThrows(EntityNotFoundException.class, () -> momentServiceImpl.getById(momentId));
    }

    @Test
    public void getByIdReturnsMoment() {
        long momentId = 1;
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(new Moment()));

        momentServiceImpl.getById(momentId);

        verify(momentRepository, times(1)).findById(momentId);
    }

    @Test
    public void getAllMomentsReturnsMoments() {
        momentServiceImpl.getAllMoments();

        verify(momentRepository, times(1)).findAll();
    }

    @Test
    public void getAllMomentsByProjectIdReturnsMoments() {
        long projectId = 1;
        momentServiceImpl.getMomentsByProjectId(projectId);

        verify(momentRepository, times(1)).findAllByProjectId(projectId);
    }

    @Test
    public void getAllMomentsByMonthReturnsMoments() {
        SearchMomentDto searchMomentDto = new SearchMomentDto(
                1L,
                "name",
                "description",
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                List.of(1L, 2L, 3L),
                List.of(100L, 101L, 102L),
                LocalDateTime.of(2026, Month.JANUARY, 4, 12,0),
                LocalDateTime.of(2026, Month.JANUARY, 5, 12,0),
                100L,
                101L
        );
        Moment moment = new Moment();
        moment.setCreatedAt(LocalDateTime.of(2026, Month.JANUARY, 5, 12,0));
        when(momentRepository.findAll()).thenReturn(List.of(moment));
        Iterator<MomentFilter> momentFilterIterator = new LazyIteratorChain<>() {
            @Override
            protected Iterator<? extends MomentFilter> nextIterator(int i) {
                return null;
            }
        };
        when(momentFilters.iterator()).thenReturn(momentFilterIterator);

        momentServiceImpl.getMomentsByMonth(searchMomentDto);

        verify(momentRepository, times(1)).findAll();
        for (MomentFilter momentFilter : momentFilters) {
            verify(momentFilter, times(1)).isAplicable(searchMomentDto);
        }
        for (MomentFilter momentFilter : momentFilters) {
            verify(momentFilter, times(1)).apply(Stream.of(moment), searchMomentDto);
        }
    }

    @Test
    public void deleteByIdNonexistentMoment() {
        long momentId = 2;

        doThrow(new RuntimeException()).when(momentRepository).deleteById(momentId);

        momentServiceImpl.deleteById(momentId);

        verify(momentRepository, times(1)).deleteById(momentId);
    }

    @Test
    public void deleteByIdDeletesMoment() {
        long momentId = 2;

        momentServiceImpl.deleteById(momentId);

        verify(momentRepository, times(1)).deleteById(momentId);
    }
}
