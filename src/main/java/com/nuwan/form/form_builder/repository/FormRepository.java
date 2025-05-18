package com.nuwan.form.form_builder.repository;

import com.nuwan.form.form_builder.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, UUID> {
    List<Form> findFormBySlugAndSection_Id(String slug, UUID sectionId);
}
