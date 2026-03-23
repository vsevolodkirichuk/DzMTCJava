package com.example.todolist.service;

import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.repository.TaskAttachmentRepository;
import com.example.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {
    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final Path uploadDir;

    public AttachmentService(TaskAttachmentRepository attachmentRepository,
                             TaskRepository taskRepository,
                             @Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) throws IOException {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = uploadDir.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());

        return attachmentRepository.save(attachment);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + attachmentId));
    }

    public Resource loadAsResource(Long attachmentId) throws MalformedURLException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("File not found: " + attachment.getStoredFileName());
        }
        return resource;
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadDir.resolve(attachment.getStoredFileName()).normalize();
        Files.deleteIfExists(filePath);
        attachmentRepository.deleteById(attachmentId);
    }

    public List<TaskAttachment> getByTaskId(Long taskId) {
        return attachmentRepository.findByTaskId(taskId);
    }
}
