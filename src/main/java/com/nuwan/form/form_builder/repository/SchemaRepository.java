package com.nuwan.form.form_builder.repository;

import com.nuwan.form.form_builder.model.Schema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SchemaRepository extends JpaRepository<Schema, UUID> {
    Optional<Schema> findByNameAndVersion(String name, Long version);
}
