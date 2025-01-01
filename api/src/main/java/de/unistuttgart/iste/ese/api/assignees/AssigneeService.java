package de.unistuttgart.iste.ese.api.assignees;

import de.unistuttgart.iste.ese.api.todos.Todo;
import de.unistuttgart.iste.ese.api.todos.TodoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/**
 * Service for handling business logic related to Assignees.
 * provides CRUD methods
 */
@Service
public class AssigneeService {

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private TodoRepository todoRepository;

    /**
     * Creates a new Assignee
     *
     * @param assignee The Assignee object to create.
     * @return The created Assignee.
     * @throws ResponseStatusException If the validation fails.
     */
    public Assignee createAssignee(@Valid Assignee assignee) {
        return assigneeRepository.save(assignee);
    }

    /**
     * Retrieves all Assignees from the repository
     *
     * @return A list of all Assignees.
     */
    public List<Assignee> getAllAssignees() {
        return assigneeRepository.findAll();
    }

    /**
     * Retrieves an Assignee by their id
     *
     * @param id The ID of the Assignee to retrieve.
     * @return The requested Assignee
     * @throws ResponseStatusException If the Assignee is not found.
     */
    public Assignee getAssigneeById(Long id) {
        return assigneeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
    }

    /**
     * Updates an existing Assignee identified by its Id
     *
     * @param id The Id of the Assignee to update.
     * @param assigneeDetails The new details for the Assignee.
     * @return The updated Assignee
     * @throws ResponseStatusException If the Assignee is not found or validation fails
     */
    public Assignee updateAssignee(Long id, @Valid Assignee assigneeDetails) {
        Assignee assignee = assigneeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
        assignee.setPrename(assigneeDetails.getPrename());
        assignee.setName(assigneeDetails.getName());
        assignee.setEmail(assigneeDetails.getEmail());
        return assigneeRepository.save(assignee);
    }

    /**
     * Deletes an Assignee by their Id and updates associated Todos
     *
     * @param id The ID of the Assignee to delete.
     * @throws ResponseStatusException If the Assignee is not found.
     */
    public void deleteAssignee(Long id) {
        Assignee assignee = assigneeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));

        List<Todo> correspondingTodos = todoRepository.findAllByAssigneeList(assignee);
        for (Todo todo : correspondingTodos) {
            todo.getAssigneeList().remove(assignee);
            todoRepository.save(todo);
        }
        assigneeRepository.delete(assignee);
    }
    
}
