package faang.school.projectservice.service.google_calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
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
@Service
@Slf4j
public class AclService {
    @Lazy
    @Autowired
    private Calendar calendarClient;

    public void grantAccessToCalendar(String calendarId, String userEmail, CalendarAclRole role, CalendarAclScopeType scopeType) {
        log.info("Предоставление доступа к календарю с ID '{}' для пользователя '{}' с ролью '{}' и областью '{}'",
                calendarId, userEmail, role, scopeType);

        try {
            AclRule rule = new AclRule();
            rule.setRole(role.getRole());

            AclRule.Scope scope = new AclRule.Scope();
            scope.setType(scopeType.getScopeType());
            scope.setValue(userEmail);
            rule.setScope(scope);

            calendarClient.acl().insert(calendarId, rule).execute();

            log.info("Доступ предоставлен");
        } catch (IOException e) {
            log.error("Ошибка предоставления доступа к календарю", e);
            throw new GoogleCalendarException("Ошибка предоставления доступа к календарю", e);
        }
    }

    public List<AclRule> getCalendarAcl(String calendarId) {
        log.info("Получение списка правил доступа для календаря с ID '{}'", calendarId);

        try {
            List<AclRule> aclRules = calendarClient.acl().list(calendarId).execute().getItems();

            log.info("Найдено '{}' правил доступа", aclRules.size());

            return aclRules;
        } catch (IOException e) {
            log.error("Ошибка получения списка правил доступа", e);
            throw new GoogleCalendarException("Ошибка получения списка правил доступа", e);
        }
    }

    public void deleteCalendarAcl(String calendarId, String ruleId) {
        log.info("Удаление правила доступа с ID '{}' для календаря '{}'", ruleId, calendarId);

        try {
            calendarClient.acl().delete(calendarId, ruleId).execute();

            log.info("Правило доступа удалено");
        } catch (IOException e) {
            log.error("Ошибка удаления права доступа", e);
            throw new GoogleCalendarException("Ошибка удаления права доступа", e);
        }
    }
}