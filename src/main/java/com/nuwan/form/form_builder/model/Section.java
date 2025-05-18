package com.nuwan.form.form_builder.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "section",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"title", "sort_order", "schema_id"}, name = "uk_title_sort_order_schema_id")
        }
)
public class Section extends BaseModel {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id", nullable = false)
    private Schema schema;
    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Form> forms = new ArrayList<>();

    public void addForm(Form form) {
        forms.add(form);
        form.setSection(this);
    }

    public void removeForm(Form form) {
        forms.remove(form);
        form.setSection(null);
    }
}
