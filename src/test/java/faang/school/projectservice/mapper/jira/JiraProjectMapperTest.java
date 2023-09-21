package faang.school.projectservice.mapper.jira;

import faang.school.projectservice.dto.jira.JiraProjectDto;
import faang.school.projectservice.model.jira.JiraProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JiraProjectMapperTest {

    @Spy
    private JiraProjectMapperImpl jiraProjectMapper;
    private JiraProject jiraProjectExpected;
    private JiraProjectDto jiraProjectDtoExpected;

    @BeforeEach
    void setUp() {
        jiraProjectExpected = JiraProject.builder()
                .id(1L)
                .key("KEY")
                .name("Name")
                .username("Username")
                .password("Password")
                .url("URL")
                .build();

        jiraProjectDtoExpected = JiraProjectDto.builder()
                .id(1L)
                .key("KEY")
                .name("Name")
                .username("Username")
                .password("Password")
                .url("URL")
                .build();
    }

    @Test
    void toEntity_shouldMatchAllFields() {
        JiraProject actual = jiraProjectMapper.toEntity(jiraProjectDtoExpected);
        assertEquals(jiraProjectExpected, actual);
    }

    @Test
    void toDto_shouldMatchAllFields() {
        JiraProjectDto actual = jiraProjectMapper.toDto(jiraProjectExpected);
        assertEquals(jiraProjectDtoExpected, actual);
    }
}