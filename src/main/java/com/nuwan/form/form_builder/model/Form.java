package com.nuwan.form.form_builder.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "form",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"title", "slug", "section_id"}, name = "uk_title_slug_section_id")
        }
)
public class Form extends BaseModel {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
    @Getter
    @Setter
    private String slug;
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "form", cascade = CascadeType.ALL)
    private List<QuestionGroup> questionGroups = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
        question.setForm(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setForm(null);
    }

    public void addGroup(QuestionGroup questionGroup) {
        questionGroups.add(questionGroup);
        questionGroup.setForm(this);
    }

    public void removeGroup(QuestionGroup questionGroup) {
        questionGroups.remove(questionGroup);
        questionGroup.setForm(null);
    }

}
