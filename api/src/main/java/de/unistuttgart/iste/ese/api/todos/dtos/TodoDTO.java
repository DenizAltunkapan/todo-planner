package de.unistuttgart.iste.ese.api.todos.dtos;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class TodoDTO {

    @NotNull(message = "Title is required")
    @Size(min = 1, message = "Title must be at least one character long")
    private String title;

    private String description;

    private boolean finished;

    private List<Long> assigneeIdList;

    private Long dueDate;

    private Long finishedDate;

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

    public List<Long> getAssigneeIdList() {
        return assigneeIdList;
    }

    public void setAssigneeIdList(List<Long> assigneeIdList) {
        this.assigneeIdList = assigneeIdList;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public Long getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Long finishedDate) {
        this.finishedDate = finishedDate;
    }
}
