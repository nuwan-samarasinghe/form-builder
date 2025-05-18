package com.nuwan.form.form_builder.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "schema",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "version"}, name = "uk_schema_name_version")
        }
)
public class Schema extends BaseModel {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    @Version
    private Long version;
    @Getter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
        section.setSchema(this);
    }

    public void removeSection(Section section) {
        sections.remove(section);
        section.setSchema(null);
    }
}
