package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.model.CalendarAclRole;
import faang.school.projectservice.model.CalendarAclScopeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AclServiceTest {
    private static final String CALENDAR_ID = "test-calendar-id";
    private static final String USER_EMAIL = "user@example.com";
    private static final String RULE_ID = "rule-id";
    private static final CalendarAclRole ROLE = CalendarAclRole.OWNER;
    private static final CalendarAclScopeType SCOPE_TYPE = CalendarAclScopeType.USER;
    private static final String TEST_IO_EXCEPTION_MESSAGE = "Test IOException";

    @Mock
    private Calendar calendarClient;

    @InjectMocks
    private AclService aclService;

    private Calendar.Acl.Insert aclInsert;
    private Calendar.Acl.Delete aclDelete;
    private Calendar.Acl.List aclList;

    @BeforeEach
    public void setUp() throws IOException {
        Calendar.Acl calendarAcl = mock(Calendar.Acl.class);
        when(calendarClient.acl()).thenReturn(calendarAcl);

        aclInsert = mock(Calendar.Acl.Insert.class);
        lenient().when(calendarAcl.insert(eq(CALENDAR_ID), any(AclRule.class))).thenReturn(aclInsert);

        aclDelete = mock(Calendar.Acl.Delete.class);
        lenient().when(calendarAcl.delete(eq(CALENDAR_ID), eq(RULE_ID))).thenReturn(aclDelete);

        aclList = mock(Calendar.Acl.List.class);
        lenient().when(calendarAcl.list(eq(CALENDAR_ID))).thenReturn(aclList);
    }

    @Test
    public void testGrantAccessToCalendar_Success() throws IOException {
        when(aclInsert.execute()).thenReturn(new AclRule());

        aclService.grantAccessToCalendar(CALENDAR_ID, USER_EMAIL, ROLE, SCOPE_TYPE);

        verify(calendarClient.acl()).insert(eq(CALENDAR_ID), any(AclRule.class));
        verify(aclInsert).execute();
    }

    @Test
    public void testGrantAccessToCalendar_IOException() throws IOException {
        when(aclInsert.execute()).thenThrow(new IOException(TEST_IO_EXCEPTION_MESSAGE));

        assertThrows(IOException.class, () ->
                aclService.grantAccessToCalendar(CALENDAR_ID, USER_EMAIL, ROLE, SCOPE_TYPE));

        verify(calendarClient.acl()).insert(eq(CALENDAR_ID), any(AclRule.class));
        verify(aclInsert).execute();
    }

    @Test
    public void testGetCalendarAcl_Success() throws IOException {
        AclRule aclRule = new AclRule();
        aclRule.setId(RULE_ID);
        aclRule.setRole("owner");

        Acl acl = new Acl();
        acl.setItems(List.of(aclRule));

        when(aclList.execute()).thenReturn(acl);
        List<AclRule> result = aclService.getCalendarAcl(CALENDAR_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RULE_ID, result.get(0).getId());

        verify(calendarClient.acl()).list(CALENDAR_ID);
        verify(aclList).execute();
    }

    @Test
    public void testGetCalendarAcl_IOException() throws IOException {
        when(aclList.execute()).thenThrow(new IOException(TEST_IO_EXCEPTION_MESSAGE));

        assertThrows(IOException.class, () ->
                aclService.getCalendarAcl(CALENDAR_ID));

        verify(calendarClient.acl()).list(CALENDAR_ID);
        verify(aclList).execute();
    }

    @Test
    public void testDeleteCalendarAcl_Success() throws IOException {
        doNothing().when(aclDelete).execute();

        aclService.deleteCalendarAcl(CALENDAR_ID, RULE_ID);

        verify(calendarClient.acl()).delete(CALENDAR_ID, RULE_ID);
        verify(aclDelete).execute();
    }

    @Test
    public void testDeleteCalendarAcl_IOException() throws IOException {
        doThrow(new IOException(TEST_IO_EXCEPTION_MESSAGE)).when(aclDelete).execute();

        assertThrows(IOException.class, () ->
                aclService.deleteCalendarAcl(CALENDAR_ID, RULE_ID));

        verify(calendarClient.acl()).delete(CALENDAR_ID, RULE_ID);
        verify(aclDelete).execute();
    }
}