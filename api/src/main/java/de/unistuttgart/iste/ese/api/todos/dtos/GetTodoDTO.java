package de.unistuttgart.iste.ese.api.todos.dtos;

import de.unistuttgart.iste.ese.api.assignees.Assignee;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public class GetTodoDTO {

    private Long id;

    @NotNull(message = "Title is required")
    @Size(min = 1, message = "Title must be at least one character long")
    private String title;

    private String description;

    private boolean finished;

    private List<Assignee> assigneeList;

    private Long createdDate;

    private Long dueDate;

    private Long finishedDate;
    
    private String category;

    public GetTodoDTO(){}
    public GetTodoDTO(Long id, String title, String description, boolean finished, List<Assignee> assigneList,
                       Long createdDate, Long dueDate, Long finishedDate, String category) {
        this.id =id;
        this.title = title;
        this.description = description;
        setFinished(finished);
        this.assigneeList = assigneList;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.finishedDate = finishedDate;
        this.category = category;
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
        if(finished) finishedDate = new Date().getTime();
        else finishedDate = null;
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

    public Long getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Long finishedDate) {
        this.finishedDate = finishedDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
