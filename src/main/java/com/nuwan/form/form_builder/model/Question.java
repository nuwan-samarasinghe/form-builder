package com.nuwan.form.form_builder.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.nuwan.form.form_builder.model.types.DataType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "question")
public class Question extends BaseModel {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String slug;
    @Getter
    @Setter
    private String hint;
    @Getter
    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode dataSource;
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private DataType dataType;
    @Getter
    @Setter
    private Integer sortOrder;
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_group_id")
    private QuestionGroup questionGroup;
    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Validation> validations = new ArrayList<>();
    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Condition> conditions = new ArrayList<>();

    public void addValidation(Validation validation) {
        validations.add(validation);
        validation.setQuestion(this);
    }

    public void removeSection(Validation validation) {
        validations.remove(validation);
        validation.setQuestion(null);
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
        condition.setQuestion(this);
    }

    public void removeCondition(Condition condition) {
        conditions.remove(condition);
        condition.setQuestion(null);
    }
}
