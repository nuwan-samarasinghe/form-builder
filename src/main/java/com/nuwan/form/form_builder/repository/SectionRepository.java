package com.nuwan.form.form_builder.repository;

import com.nuwan.form.form_builder.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
}
