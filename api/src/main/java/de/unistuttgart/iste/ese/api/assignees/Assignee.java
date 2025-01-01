package de.unistuttgart.iste.ese.api.assignees;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents an Assignee entity stored in the "assignees" table. 
 * Models a person who can be assigned to todos, with attributes like Id, name, and a validated university email address.
 */
@Entity
@Table(name = "assignees")
public class Assignee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(min = 1, message = "First name must be at least one character long")
    private String prename;

    @Size(min = 1, message = "Last name must be at least one character long")
    private String name;

    @Pattern(regexp = "^[\\w.-]+@([a-zA-Z0-9-]+\\.)*uni-stuttgart\\.de$", message = "Email must end with uni-stuttgart.de")
    private String email;
    

    public Assignee(String prename, String name, String email) {
        this.prename = prename;
        this.name = name;
        this.email = email;
    }

    public Assignee() {}
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
