package faang.school.projectservice.service;


import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Mock
    private MomentMapper momentMapper;
    @Mock
    private List<MomentFilter> momentFilters;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private MomentValidator momentValidator;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private MomentService momentService;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void testCreateMoment() {

    }

    @Test
    public void testUpdateMoment() {

    }

    @Test
    public void testGetMomentsFilteredByDateFromProjects() {

    }

    @Test
    public void testGetAllMoments() {

    }

    @Test
    public void testGetMomentById() {

    }
}
