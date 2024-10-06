package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.model.CalendarAclRole;
import faang.school.projectservice.model.CalendarAclScopeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AclService {
    @Lazy
    @Autowired
    private Calendar calendarClient;

    public void grantAccessToCalendar(String calendarId, String userEmail,
                                      CalendarAclRole role, CalendarAclScopeType scopeType) throws IOException {
        AclRule rule = new AclRule();
        rule.setRole(role.getRole());

        AclRule.Scope scope = new AclRule.Scope();
        scope.setType(scopeType.getScopeType());
        scope.setValue(userEmail);
        rule.setScope(scope);

        calendarClient.acl()
                .insert(calendarId, rule)
                .execute();
        log.info("User '{}' granted access to calendar '{}'", userEmail, calendarId);
    }

    public List<AclRule> getCalendarAcl(String calendarId) throws IOException {
        List<AclRule> aclRules = calendarClient.acl()
                .list(calendarId)
                .execute()
                .getItems();
        return aclRules;
    }

    public void deleteCalendarAcl(String calendarId, String ruleId) throws IOException {
        calendarClient.acl()
                .delete(calendarId, ruleId)
                .execute();
        log.info("Acl rule with ID '{}' was deleted", ruleId);
    }
}