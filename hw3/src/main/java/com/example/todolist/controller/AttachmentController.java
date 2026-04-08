package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Attachments", description = "File attachment API")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/api/tasks/{taskId}/attachments")
    @Operation(summary = "Upload attachment")
    public ResponseEntity<AttachmentResponseDto> upload(@PathVariable Long taskId,
                                                         @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(toDto(attachmentService.storeAttachment(taskId, file)));
    }

    @GetMapping("/api/attachments/{attachmentId}")
    @Operation(summary = "Download attachment")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) throws IOException {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/api/attachments/{attachmentId}")
    @Operation(summary = "Delete attachment")
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId) throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/tasks/{taskId}/attachments")
    @Operation(summary = "List attachments for a task")
    public ResponseEntity<List<AttachmentResponseDto>> listByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(attachmentService.getByTaskId(taskId).stream().map(this::toDto).collect(Collectors.toList()));
    }

    private AttachmentResponseDto toDto(TaskAttachment a) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(a.getId());
        dto.setFileName(a.getFileName());
        dto.setSize(a.getSize());
        dto.setUploadedAt(a.getUploadedAt());
        return dto;
    }
}
