package faang.school.projectservice.dto.candidate;

import faang.school.projectservice.model.CandidateStatus;

/**
 * DTO (объект передачи данных), представляющий кандидата, подавшего заявку на вакансию.
 * <p>
 * Содержит основную информацию о кандидате, включая идентификаторы, имя пользователя,
 * резюме, сопроводительное письмо и текущий статус заявки.
 *
 * @param id           Уникальный идентификатор записи кандидата.
 * @param userId       Идентификатор пользователя, подавшего заявку.
 * @param userName     Имя пользователя (отображаемое имя).
 * @param resumeDocKey Ключ (например, из облачного хранилища) к документу с резюме.
 * @param coverLetter  Текст сопроводительного письма кандидата.
 * @param status       Текущий статус заявки кандидата (например, ОЖИДАЕТ, ОДОБРЕН, ОТКЛОНЁН).
 * @param vacancyId    Идентификатор вакансии, на которую подана заявка.
 * @author Myrza
 * @since 20.07.2025
 */
public record CandidateDto(
        Long id,
        Long userId,
        String userName,
        String resumeDocKey,
        String coverLetter,
        CandidateStatus status,
        Long vacancyId
) {
}
