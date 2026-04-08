package com.example.todolist.service;

import com.example.todolist.dto.PriorityCount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PriorityCount> getTasksCountByPriority() {
        String sql = "SELECT priority, COUNT(*) AS count FROM tasks GROUP BY priority";

        RowMapper<PriorityCount> rowMapper = (rs, rowNum) ->
                new PriorityCount(rs.getString("priority"), rs.getLong("count"));

        return jdbcTemplate.query(sql, rowMapper);
    }
}
