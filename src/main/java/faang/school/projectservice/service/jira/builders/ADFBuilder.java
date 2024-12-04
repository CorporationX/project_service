package faang.school.projectservice.service.jira.builders;

import faang.school.projectservice.dto.jira.adf.ADFAttributes;
import faang.school.projectservice.dto.jira.adf.ADFBlock;
import faang.school.projectservice.dto.jira.adf.ADFDocument;
import faang.school.projectservice.dto.jira.adf.ADFMark;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ADFBuilder {

    private final List<ADFBlock> content = new ArrayList<>();

    public ADFDocument build() {
        return new ADFDocument(content);
    }

    public ADFBuilder addText(String text) {
        content.add(createParagraph(text));
        return this;
    }

    public ADFBuilder addHeading(String text, int level) {
        content.add(createHeading(text, level));
        return this;
    }

    public ADFBuilder addBold(String text) {
        content.add(createBoldText(text));
        return this;
    }

    public ADFBuilder addList(List<String> items) {
        content.add(createBulletList(items));
        return this;
    }

    private ADFBlock createParagraph(String text) {
        return ADFBlock.builder()
                .type("paragraph")
                .content(createContent(List.of(text)))
                .build();
    }

    private ADFBlock createHeading(String title, int level) {
        return ADFBlock.builder()
                .type("heading")
                .attrs(new ADFAttributes(level))
                .content(createContent(List.of(title)))
                .build();
    }

    private ADFBlock createBoldText(String text) {
        ADFBlock textBlock = ADFBlock.builder()
                .type("text")
                .text(text)
                .marks(List.of(new ADFMark("strong")))
                .build();

        return ADFBlock.builder()
                .type("paragraph")
                .content(List.of(textBlock))
                .build();
    }

    private ADFBlock createBulletList(List<String> items) {
        List<ADFBlock> bullets = items.stream()
                .map(this::createListItem)
                .toList();

        return ADFBlock.builder()
                .type("bulletList")
                .content(bullets)
                .build();
    }

    private ADFBlock createListItem(String text) {
        return ADFBlock.builder()
                .type("listItem")
                .content(createContent(List.of(text)))
                .build();
    }

    private List<ADFBlock> createContent(List<String> contents) {
        return contents.stream()
                .map(content -> ADFBlock.builder()
                        .type("text")
                        .text(content)
                        .build())
                .toList();
    }
}
