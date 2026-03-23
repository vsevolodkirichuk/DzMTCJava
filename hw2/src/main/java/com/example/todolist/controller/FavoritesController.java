package com.example.todolist.controller;

import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Favorites management API")
public class FavoritesController {
    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PostMapping("/{taskId}")
    @Operation(summary = "Add task to favorites")
    public ResponseEntity<Void> add(@PathVariable Long taskId, HttpSession session) {
        favoritesService.addFavorite(taskId, session);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Remove task from favorites")
    public ResponseEntity<Void> remove(@PathVariable Long taskId, HttpSession session) {
        favoritesService.removeFavorite(taskId, session);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get favorite tasks")
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        return ResponseEntity.ok(favoritesService.getFavorites(session));
    }
}
