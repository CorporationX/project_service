package faang.school.projectservice.mapper.jira;

import faang.school.projectservice.dto.jira.IssueStatusTransition;
import faang.school.projectservice.dto.jira.IssueStatusUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IssueStatusMapperTest {

    @Spy
    private IssueStatusMapperImpl issueStatusMapper;
    private IssueStatusUpdateDto issueStatusUpdateDtoExpected;
    private IssueStatusTransition issueStatusTransitionExpected;


    @BeforeEach
    void setUp() {
        issueStatusUpdateDtoExpected = IssueStatusUpdateDto.builder()
                .status("To do")
                .build();

        issueStatusTransitionExpected = IssueStatusTransition.builder()
                .transition(IssueStatusTransition.Transition.builder().id("11").build())
                .build();
    }

    @Test
    void toTransition_shouldMatch() {
        IssueStatusTransition actual = issueStatusMapper.toTransition(issueStatusUpdateDtoExpected);
        assertEquals(issueStatusTransitionExpected, actual);
    }

    @Test
    void testToTransitionId_shouldReturnTransitionId11() {
        IssueStatusTransition.Transition actual = issueStatusMapper.toTransitionId("To Do");
        IssueStatusTransition.Transition expected = new IssueStatusTransition.Transition("11");

        assertEquals(expected, actual);
    }

    @Test
    void testToTransitionId_shouldReturnTransitionId21() {
        IssueStatusTransition.Transition actual = issueStatusMapper.toTransitionId("In Progress");
        IssueStatusTransition.Transition expected = new IssueStatusTransition.Transition("21");

        assertEquals(expected, actual);
    }

    @Test
    void testToTransitionId_shouldReturnTransitionId31() {
        IssueStatusTransition.Transition actual = issueStatusMapper.toTransitionId("Done");
        IssueStatusTransition.Transition expected = new IssueStatusTransition.Transition("31");

        assertEquals(expected, actual);
    }
}