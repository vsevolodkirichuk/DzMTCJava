package com.example.todolist.controller;

import com.example.todolist.dto.AttachmentResponseDto;
import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttachmentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long taskId;

    @BeforeEach
    void setUp() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Task for attachments");
        dto.setPriority(Priority.LOW);
        dto.setDueDate(LocalDate.now().plusDays(1));
        TaskResponseDto task = restTemplate.postForEntity("/api/tasks", dto, TaskResponseDto.class).getBody();
        taskId = task.getId();
    }

    private AttachmentResponseDto uploadFile() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource("hello".getBytes()) {
            @Override
            public String getFilename() { return "test.txt"; }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity("/api/tasks/" + taskId + "/attachments", request, AttachmentResponseDto.class).getBody();
    }

    @Test
    void upload_positive_returnsAttachment() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource("hello".getBytes()) {
            @Override
            public String getFilename() { return "test.txt"; }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<AttachmentResponseDto> response = restTemplate.postForEntity(
                "/api/tasks/" + taskId + "/attachments", request, AttachmentResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void upload_negative_taskNotFound() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource("hello".getBytes()) {
            @Override
            public String getFilename() { return "test.txt"; }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/tasks/99999/attachments", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void download_positive_returnsFile() {
        AttachmentResponseDto attachment = uploadFile();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                "/api/attachments/" + attachment.getId(), byte[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void listByTask_positive_returnsList() {
        uploadFile();
        ResponseEntity<AttachmentResponseDto[]> response = restTemplate.getForEntity(
                "/api/tasks/" + taskId + "/attachments", AttachmentResponseDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void delete_positive_returnsNoContent() {
        AttachmentResponseDto attachment = uploadFile();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/attachments/" + attachment.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
