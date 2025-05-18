package com.nuwan.form.form_builder.repository;

import com.nuwan.form.form_builder.model.Schema;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class SchemaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SchemaRepository schemaRepository;

    private Faker faker;

    @BeforeEach
    public void setup() {
        faker = new Faker();
        for (int i = 0; i < 2; i++) {
            Schema schema = createSchema(faker.name().name(), faker.number().randomNumber());
            entityManager.persist(schema);
        }
        entityManager.flush();
    }

    private Schema createSchema(String name, Long version) {
        Schema schema = new Schema();
        schema.setName(name);
        schema.setVersion(version);
        schema.setCreatedAt(Timestamp.from(faker.timeAndDate().past()));
        schema.setUpdatedAt(Timestamp.from(Instant.now()));
        return schema;
    }

    @Test
    @DisplayName("Should retrieve schema by ID when present")
    public void testGetSchemaById() {
        String name = "TestSchema";
        Long version = 100L;
        Schema schema = createSchema(name, version);
        Schema savedSchema = entityManager.persistAndFlush(schema);

        Optional<Schema> searchedSchema = schemaRepository.findById(savedSchema.getId());

        assertThat(searchedSchema).isPresent();
        assertThat(searchedSchema.get().getName()).isEqualTo(name);
        assertThat(searchedSchema.get().getVersion()).isEqualTo(version);
    }

    @Test
    @DisplayName("Should return empty when schema ID not found")
    public void testGetSchemaByIdNotFound() {
        Optional<Schema> searchedSchema = schemaRepository.findById(UUID.randomUUID());
        assertThat(searchedSchema).isEmpty();
    }

    @Test
    @DisplayName("Should retrieve all schemas")
    public void testGetAllSchemas() {
        List<Schema> schemas = schemaRepository.findAll();
        assertThat(schemas).isNotEmpty();
        assertThat(schemas.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should find schema by name and version when present")
    public void testFindSchemaByNameAndVersion() {
        String schemaName = "FindMeSchema";
        Long schemaVersion = 200L;
        Schema schema = createSchema(schemaName, schemaVersion);
        entityManager.persistAndFlush(schema);

        Optional<Schema> schemaOptional = schemaRepository.findByNameAndVersion(schemaName, schemaVersion);

        assertThat(schemaOptional).isPresent();
        assertThat(schemaOptional.get().getName()).isEqualTo(schemaName);
        assertThat(schemaOptional.get().getVersion()).isEqualTo(schemaVersion);
    }

    @Test
    @DisplayName("Should return empty when schema with name and version not found")
    public void testFindSchemaByNameAndVersionNotAvailable() {
        Optional<Schema> schema = schemaRepository.findByNameAndVersion("NonExistent", 999L);
        assertThat(schema).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when schema with name and version not found")
    public void testFindSchemaByNameAndVersionNotFound() {
        Optional<Schema> schema = schemaRepository.findByNameAndVersion("NonExistent", null);
        assertThat(schema).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when schema with name and version not found")
    public void testFindSchemaByNameAndVersionNotFound2() {
        Optional<Schema> schema = schemaRepository.findByNameAndVersion(null, 999L);
        assertThat(schema).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when schema with name and version not found")
    public void testFindSchemaByNameAndVersionNotFound3() {
        Optional<Schema> schema = schemaRepository.findByNameAndVersion(null, null);
        assertThat(schema).isEmpty();
    }

    @Test
    @DisplayName("Should successfully persists the schema when added")
    public void testSaveSchema() {
        schemaRepository.save(createSchema("TestSchema", 100L));
        List<Schema> schemas = schemaRepository.findAll();
        assertThat(schemas).isNotEmpty();
        assertThat(schemas.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should successfully delete the schema when removed")
    public void testDeleteSchema() {
        Schema schema = createSchema("TestSchema", 100L);
        Schema savedSchema = entityManager.persistAndFlush(schema);
        schemaRepository.delete(savedSchema);
    }

    @Test
    @DisplayName("Should successfully update schema")
    public void testUpdateSchema() {
        Schema schema = createSchema("TestSchema", 100L);
        Schema savedSchema = entityManager.persistAndFlush(schema);
        savedSchema.setName("UpdatedSchema");
        savedSchema.setVersion(200L);
        savedSchema.setUpdatedAt(Timestamp.from(Instant.now()));
        Schema updatedSchema = schemaRepository.save(savedSchema);
        assertThat(updatedSchema.getName()).isEqualTo("UpdatedSchema");
        assertThat(updatedSchema.getVersion()).isEqualTo(200L);
    }

    @Test
    @DisplayName("Should not be able to add same schema name and version")
    public void testAddNewSchemaWithSameNameAndSameVersionShouldThrowException() {
        Schema schema = createSchema("This is a same schema name", 100L);
        entityManager.persistAndFlush(schema);
        assertThrows(DataIntegrityViolationException.class, () -> {
            Schema schema2 = createSchema("This is a same schema name", 100L);
            schemaRepository.saveAndFlush(schema2);
        });
    }
}
