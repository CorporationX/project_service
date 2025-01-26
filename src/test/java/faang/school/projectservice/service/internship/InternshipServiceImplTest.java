package faang.school.projectservice.service.internship;

import faang.school.projectservice.adapter.InternshipRepositoryAdapter;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceImplTest {

    @Mock
    private InternshipMapper internshipMapper;
    @Mock
    private InternshipRepositoryAdapter internshipRepositoryAdapter;
    @Mock
    private List<InternshipFilter> internshipFilters;

    private static final Integer NUMBER_INVOCATION = 1;
    private static final Long INTERNSHIP_ID = 1L;

    @InjectMocks
    InternshipServiceImpl internshipService;

    @Test
    public void testUpdateInternship(){
        //when completed
//        when new mentor
//        IsAheadOfSchedule
    }
    @Test
    public void testGetInternshipsWithFilters() {
        InternshipFilterDto internshipFilterDto = new InternshipFilterDto();
        internshipFilterDto.setStatus(InternshipStatus.COMPLETED);
        Stream<Internship> internships = prepareStreamOfInternships();
        internshipFilters.add(new InternshipStatusFilter());
        when(internshipRepositoryAdapter.findAll()).thenReturn(internships);
        internshipService.getInternshipsWithFilters(internshipFilterDto);
        Mockito.verify(internshipFilters, Mockito.times(NUMBER_INVOCATION)).stream();
    }

    @Test
    public void testGetAllInternships() {
        internshipService.getAllInternships();
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).findAll();
    }

    @Test
    public void testGetInternship() {
        internshipService.getInternship(INTERNSHIP_ID);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).findById(INTERNSHIP_ID);
    }

    private Stream<Internship> prepareStreamOfInternships() {
        List<Internship> internships = fillListOfInternships();
        return internships.stream();
    }

    private List<Internship> fillListOfInternships() {
        List<Internship> internships = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Internship internship = new Internship();
            internship.setId((long) i);
            if (i < 5) {
                internship.setStatus(InternshipStatus.COMPLETED);
            }
            internships.add(internship);
        }
        return internships;
    }
}
