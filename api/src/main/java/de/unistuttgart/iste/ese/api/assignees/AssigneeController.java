package de.unistuttgart.iste.ese.api.assignees;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/**
 * Handles CRUD operations for Assignees.
 * Delegates business logic to the AssigneeService
 */
@RestController
@ApiVersion1
public class AssigneeController {

    @Autowired
    private AssigneeService assigneeService;

    /**
     * Creates a new Assignee by delegating to the createAssignee method in the AssigneeService.
     * Validates the input using @Valid and handles validation errors.
     *
     * @param assignee The Assignee object containing the details of the new Assignee.
     * @param bindingResult Contains the validation results.
     * @return The created Assignee object.
     * @throws ResponseStatusException If validation errors are present in the input.
     */
    @PostMapping("/assignees")
    @ResponseStatus(HttpStatus.CREATED)
    public Assignee createAssignee(@Valid @RequestBody Assignee assignee, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid: " + bindingResult.getAllErrors());
        }
        return assigneeService.createAssignee(assignee);
    }

    /**
     * Retrieves all Assignees by calling the getAllAssignees method in the AssigneeService
     * @return A list of all Assignee objects.
     */
    @GetMapping("/assignees")
    public List<Assignee> getAllAssignees() {
        return assigneeService.getAllAssignees();
    }

    /**
     * Retrieves a specific Assignee by its ID by calling the getAssigneeById method in the AssigneeService.
     *
     * @param id The ID of the Assignee to retrieve.
     * @return The Assignee object with the given ID.
     * @throws ResponseStatusException If no Assignee with the given ID is found.
     */
    @GetMapping("/assignees/{id}")
    public Assignee getAssigneeById(@PathVariable Long id) {
        return assigneeService.getAssigneeById(id);
    }

    /**
     * Updates an existing Assignee identified by its id with the updateAssignee method in the AssigneeService
     *
     * @param id The ID of the Assignee to update.
     * @param assigneeDetails The updated Assignee details
     * @param bindingResult Contains the validation results.
     * @return The updated Assignee object
     * @throws ResponseStatusException If validation errors are present or if no Assignee with the given ID is found.
     */
    @PutMapping("/assignees/{id}")
    public Assignee updateAssignee(@PathVariable Long id, @Valid @RequestBody Assignee assigneeDetails, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid: " + bindingResult.getAllErrors());
        }
        return assigneeService.updateAssignee(id, assigneeDetails);
    }

    /**
     * Deletes an Assignee identified by its id by calling the deleteAssignee method in the AssigneeService.
     *
     * @param id The ID of the Assignee to delete.
     * @throws ResponseStatusException If no Assignee with the given ID is found.
     */
    @DeleteMapping("/assignees/{id}")
    public void deleteAssignee(@PathVariable Long id) {
        assigneeService.deleteAssignee(id);
    }
}
