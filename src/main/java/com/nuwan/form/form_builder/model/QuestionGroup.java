package com.nuwan.form.form_builder.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "question_group",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"title", "slug"}, name = "uk_title_slug")
        }
)
public class QuestionGroup extends BaseModel {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String slug;
    @Getter
    @Setter
    private Boolean allowAddAnother;
    @Getter
    @Setter
    private Boolean showAllOnSamePage;
    @Getter
    @Setter
    private Integer itemLimit;
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionGroup", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuestionGroup(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setQuestionGroup(null);
    }
}
