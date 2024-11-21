package de.unistuttgart.iste.ese.api.todos;

import de.unistuttgart.iste.ese.api.assignees.Assignee;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

/**
 * Represents a Todo item stored in the "todos" table. 
 * Includes details like title, description, status, assignees, 
 * and relevant timestamps (created, due, and finished dates).
 */
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(min = 1, message = "Title must be at least one character long")
    private String title;

    private String description;

    private boolean finished;

    @ManyToMany
    @JoinTable(
        name = "todo_assignee",
        joinColumns = @JoinColumn(name = "todo_id"),
        inverseJoinColumns = @JoinColumn(name = "assignee_id")
    )
    private List<Assignee> assigneeList;
    
    private Date createdDate;

    private Date dueDate;

    private Date finishedDate;

    public Todo(String title, String description, boolean finished, List<Assignee> assigneList,
                Date createdDate, Date dueDate, Date finishedDate) {
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.assigneeList = assigneList;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.finishedDate = finishedDate;
    }
    
    public Todo(){}

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
        if(finished) this.finishedDate = new Date();
        this.finished = finished;
    }

    public List<Assignee> getAssigneeList() {
        return assigneeList;
    }

    public void setAssigneList(List<Assignee> assigneList) {
        this.assigneeList = assigneList;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }
}
