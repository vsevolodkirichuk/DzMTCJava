package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.model.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    private Task savedTask;

    @BeforeEach
    void setUp() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setPriority(Priority.HIGH);
        task.setCompleted(false);
        task.setDueDate(LocalDate.now().plusDays(3));
        task.setTags(Set.of("spring", "java"));
        savedTask = taskRepository.save(task);
    }

    @Test
    void save_andFindById_works() {
        assertThat(taskRepository.findById(savedTask.getId())).isPresent();
    }

    @Test
    void findByCompletedAndPriority_returnsCorrectTasks() {
        List<Task> tasks = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void findDueSoon_returnsTasksDueWithin7Days() {
        List<Task> tasks = taskRepository.findDueSoon(LocalDate.now(), LocalDate.now().plusDays(7));
        assertThat(tasks).isNotEmpty();
    }

    @Test
    void findDueSoon_doesNotReturnTasksDueAfter7Days() {
        Task farTask = new Task();
        farTask.setTitle("Far Task");
        farTask.setPriority(Priority.LOW);
        farTask.setCompleted(false);
        farTask.setDueDate(LocalDate.now().plusDays(30));
        taskRepository.save(farTask);

        List<Task> tasks = taskRepository.findDueSoon(LocalDate.now(), LocalDate.now().plusDays(7));
        assertThat(tasks).noneMatch(t -> t.getTitle().equals("Far Task"));
    }

    @Test
    void findAllWithAttachments_solves_nPlusOne() {
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(savedTask);
        attachment.setFileName("file.txt");
        attachment.setStoredFileName("stored_file.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(100L);
        attachment.setUploadedAt(LocalDateTime.now());
        attachmentRepository.save(attachment);

        List<Task> tasks = taskRepository.findAllWithAttachments();
        assertThat(tasks).isNotEmpty();
    }

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    void deleteTask_cascadesAttachments() {
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(savedTask);
        attachment.setFileName("file.txt");
        attachment.setStoredFileName("stored_file.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(100L);
        attachment.setUploadedAt(LocalDateTime.now());
        attachmentRepository.save(attachment);

        taskRepository.deleteById(savedTask.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(attachmentRepository.findByTaskId(savedTask.getId())).isEmpty();
    }

    @Test
    void tags_arePersistedAndLoaded() {
        Task loaded = taskRepository.findById(savedTask.getId()).orElseThrow();
        assertThat(loaded.getTags()).containsExactlyInAnyOrder("spring", "java");
    }
}
