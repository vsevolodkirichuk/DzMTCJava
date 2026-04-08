package com.example.todolist.service;

import com.example.todolist.dto.TaskResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoritesService {

    private static final String SESSION_KEY = "favoriteTaskIds";

    private final TaskService taskService;

    public FavoritesService(TaskService taskService) {
        this.taskService = taskService;
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getFavoriteIds(HttpSession session) {
        Set<Long> ids = (Set<Long>) session.getAttribute(SESSION_KEY);
        if (ids == null) {
            ids = new HashSet<>();
            session.setAttribute(SESSION_KEY, ids);
        }
        return ids;
    }

    public void addFavorite(Long taskId, HttpSession session) {
        taskService.findEntityById(taskId);
        getFavoriteIds(session).add(taskId);
    }

    public void removeFavorite(Long taskId, HttpSession session) {
        getFavoriteIds(session).remove(taskId);
    }

    public List<TaskResponseDto> getFavorites(HttpSession session) {
        return getFavoriteIds(session).stream()
                .map(taskService::getById)
                .collect(Collectors.toList());
    }
}
