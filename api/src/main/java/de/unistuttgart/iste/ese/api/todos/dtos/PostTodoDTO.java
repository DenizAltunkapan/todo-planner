package de.unistuttgart.iste.ese.api.todos.dtos;

import de.unistuttgart.iste.ese.api.assignees.Assignee;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PostTodoDTO {
    
    private Long id;
    
    @NotNull(message = "Title is required")
    @Size(min = 1, message = "Title must be at least one character long")
    private String title;

    private String description;

    private boolean finished;

    private List<Assignee> assigneeList;
    
    private Long createdDate;

    private Long dueDate;

    public PostTodoDTO(){}
    public PostTodoDTO(Long id, String title, String description, boolean finished, List<Assignee> assigneList,
                       Long createdDate, Long dueDate) {
        this.id =id;
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.assigneeList = assigneList;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public List<Assignee> getAssigneeList() {
        return assigneeList;
    }

    public void setAssigneeList(List<Assignee> assigneeList) {
        this.assigneeList = assigneeList;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }
}
