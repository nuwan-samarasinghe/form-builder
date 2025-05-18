package com.nuwan.form.form_builder.repository;

import com.github.slugify.Slugify;
import com.nuwan.form.form_builder.model.Form;
import com.nuwan.form.form_builder.model.Schema;
import com.nuwan.form.form_builder.model.Section;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class FormRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FormRepository formRepository;

    private Faker faker;
    Section section;
    private final Slugify slg = Slugify.builder().build();

    @BeforeEach
    public void setup() {
        faker = new Faker();
        Schema schema = new Schema();
        schema.setName(faker.name().name());
        schema.setVersion(faker.number().randomNumber());
        schema.setCreatedAt(Timestamp.from(faker.timeAndDate().past()));
        schema.setUpdatedAt(Timestamp.from(Instant.now()));
        entityManager.persist(schema);

        section = new Section();
        section.setTitle(faker.name().name());
        section.setSchema(schema);
        section.setSortOrder(1);
        section.setCreatedAt(Timestamp.from(faker.timeAndDate().past()));
        section.setUpdatedAt(Timestamp.from(Instant.now()));
        section = entityManager.persist(section);

        for (int i = 0; i < 3; i++) {
            Form form = new Form();
            form.setTitle(faker.name().name());
            form.setSection(section);
            form.setCreatedAt(Timestamp.from(faker.timeAndDate().past()));
            form.setUpdatedAt(Timestamp.from(Instant.now()));
            form.setSlug(slg.slugify("This is a test form " + i));
            form.setSortOrder(i);
            entityManager.persist(form);
        }
        entityManager.flush();
    }

    @Test
    @DisplayName("Should retrieve form by slug")
    public void testRetrieveFormBySlug() {
        List<Form> formBySlug = formRepository.findFormBySlugAndSection_Id(slg.slugify("This is a test form 0"), section.getId());
        assert formBySlug.size() == 1;
        assert formBySlug.getFirst().getSlug().equals(slg.slugify("This is a test form 0"));
    }

    @Test
    @DisplayName("Should give empty list if given slug is not available")
    public void testRetrieveFormBySlugNotFound() {
        List<Form> formBySlug = formRepository.findFormBySlugAndSection_Id(slg.slugify("This is a test form 999"), section.getId());
        assert formBySlug.isEmpty();
    }

    @Test
    @DisplayName("Should remove from by slug")
    public void testRemoveFormBySlug() {
        Form form = formRepository.findFormBySlugAndSection_Id(slg.slugify("This is a test form 1"), section.getId()).getFirst();
        formRepository.delete(form);
        List<Form> formBySlug = formRepository.findFormBySlugAndSection_Id(slg.slugify("This is a test form 1"), section.getId());
        assert formBySlug.isEmpty();
    }

    @Test
    @DisplayName("Should be able to update a form for a given slug")
    public void testUpdateFormBySlug() {
        Form form = formRepository.findFormBySlugAndSection_Id(slg.slugify("This is a test form 2"), section.getId()).getFirst();
        form.setTitle("UpdatedForm");
        form.setSlug(slg.slugify("This is a test form 2"));
        form.setSortOrder(1);
        form.setUpdatedAt(Timestamp.from(Instant.now()));
        Form updatedForm = formRepository.save(form);
        assert updatedForm.getTitle().equals("UpdatedForm");
        assert updatedForm.getSlug().equals(slg.slugify("This is a test form 2"));
    }

    @Test
    @DisplayName("Should be able to add a new form for a given slug")
    public void testAddNewForm() {
        Form form = new Form();
        form.setTitle("NewForm");
        form.setSection(section);
        form.setSlug(slg.slugify("This is a test form 2"));
        form.setSortOrder(1);
        form.setCreatedAt(Timestamp.from(Instant.now()));
        form.setUpdatedAt(Timestamp.from(Instant.now()));
        Form savedForm = formRepository.save(form);
        assert savedForm.getTitle().equals("NewForm");
        assert savedForm.getSlug().equals(slg.slugify("This is a test form 2"));
    }

    @Test
    @DisplayName("Should not be able to add same form for a given section with title and same order and same slug")
    public void testAddNewFormWithSameTitleAndSameOrderAndSameSlugShouldThrowException() {
        Form form = new Form();
        form.setTitle("This is a same form title");
        form.setSection(section);
        form.setSlug(slg.slugify("This is a same slug"));
        form.setSortOrder(1);
        form.setCreatedAt(Timestamp.from(Instant.now()));
        form.setUpdatedAt(Timestamp.from(Instant.now()));
        formRepository.saveAndFlush(form);

        assertThrows(DataIntegrityViolationException.class, () -> {
            Form form2 = new Form();
            form2.setTitle("This is a same form title");
            form2.setSection(section);
            form2.setSlug(slg.slugify("This is a same slug"));
            form2.setSortOrder(2);
            form2.setCreatedAt(Timestamp.from(Instant.now()));
            form2.setUpdatedAt(Timestamp.from(Instant.now()));
            formRepository.saveAndFlush(form2);
        });
    }

}
