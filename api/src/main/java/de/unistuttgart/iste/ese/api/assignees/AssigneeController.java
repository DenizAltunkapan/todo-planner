package de.unistuttgart.iste.ese.api.assignees;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import de.unistuttgart.iste.ese.api.todos.Todo;
import de.unistuttgart.iste.ese.api.todos.TodoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Handles CRUD operations for Assignees. 
 * Exposes endpoints for creating, retrieving, updating, and deleting Assignees.
 */
@RestController
@ApiVersion1
public class AssigneeController {

    @Autowired
    private AssigneeRepository assigneeRepository;
    @Autowired
    private TodoRepository todoRepository;

//    @PostConstruct
//    public void init() {
//        long numberOfAssignees = assigneeRepository.count();
//        if(numberOfAssignees==0){
//            Assignee assignee = new Assignee("Ana Cristina", "Franco da Silva", "ana-cristina.franco-da-silva@iste.uni-stuttgart.de");
//            assigneeRepository.save(assignee);
//        }
//    }

    @PostMapping("/assignees")
    @ResponseStatus(HttpStatus.CREATED)
    public Assignee createAssignee(@Valid @RequestBody Assignee assignee, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid: " + bindingResult.getAllErrors());
        }
        return assigneeRepository.save(assignee);
    }

    @GetMapping("/assignees")
    public List<Assignee> getAllAssignees() {
        return assigneeRepository.findAll();
    }

    @GetMapping("/assignees/{id}")
    public Assignee getAssigneeById(@PathVariable Long id) {
        return assigneeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/assignees/{id}")
    public Assignee updateAssignee(@PathVariable Long id,@Valid @RequestBody Assignee assigneeDetails, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid: " + bindingResult.getAllErrors());
        }
        Assignee assignee = assigneeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        assignee.setPrename(assigneeDetails.getPrename());
        assignee.setName(assigneeDetails.getName());
        assignee.setEmail(assigneeDetails.getEmail());
        return assigneeRepository.save(assignee);
    }

    @DeleteMapping("/assignees/{id}")
    public void deleteAssignee(@PathVariable Long id) {
        Assignee assignee = assigneeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        List<Todo> correspondingTodos = todoRepository.findAllByAssigneeList(assignee);
        for(Todo todo: correspondingTodos){
            todo.getAssigneeList().remove(assignee);
            todoRepository.save(todo);
        }
        assigneeRepository.delete(assignee);
    }
}
