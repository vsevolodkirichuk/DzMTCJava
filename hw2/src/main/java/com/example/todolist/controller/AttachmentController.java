package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.model.TaskAttachment;
import com.example.todolist.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", description = "File uploaded")
    public ResponseEntity<AttachmentResponseDto> upload(@PathVariable Long taskId,
                                                         @RequestParam("file") MultipartFile file) throws IOException {
        TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.ok(toDto(attachment));
    }

    @GetMapping("/api/attachments/{attachmentId}")
    @Operation(summary = "Download attachment")
    @ApiResponse(responseCode = "200", description = "File content")
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
    @ApiResponse(responseCode = "204", description = "File deleted")
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId) throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/tasks/{taskId}/attachments")
    @Operation(summary = "List attachments for a task")
    @ApiResponse(responseCode = "200", description = "List of attachments")
    public ResponseEntity<List<AttachmentResponseDto>> listByTask(@PathVariable Long taskId) {
        List<AttachmentResponseDto> dtos = attachmentService.getByTaskId(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private AttachmentResponseDto toDto(TaskAttachment attachment) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());
        return dto;
    }
}
