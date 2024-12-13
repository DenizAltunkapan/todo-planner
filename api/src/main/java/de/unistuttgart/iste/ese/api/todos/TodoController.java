package de.unistuttgart.iste.ese.api.todos;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import de.unistuttgart.iste.ese.api.assignees.Assignee;
import de.unistuttgart.iste.ese.api.assignees.AssigneeRepository;
import de.unistuttgart.iste.ese.api.todos.dtos.GetTodoDTO;
import de.unistuttgart.iste.ese.api.todos.dtos.PostTodoDTO;
import de.unistuttgart.iste.ese.api.todos.dtos.TodoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handles CRUD operations for todos.
 * Exposes endpoints for creating, retrieving, updating, and deleting Todos.
 */
@RestController
@ApiVersion1
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private AssigneeRepository assigneeRepository;
    
    private TodoModel todoModel = new TodoModel("model.pmml");

    /**
     * Converts the provided TodoDTO into a Todo entity, including validation of assignee IDs.
     * The Todo is saved in the repository, and a response with a PostDTO is returned.
     *
     * @param todoDTO The TodoDTO containing the details of the new Todo and all dates as Unix timestamp
     * @return A PostTodoDTO representing the created Todo item.
     */
    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public PostTodoDTO createTodo(@Valid @RequestBody TodoDTO todoDTO) {
        List<Assignee> assigneeList = convertDtoToAssigneeList(todoDTO.getAssigneeIdList());
        
        Date dueDate = todoDTO.getDueDate() != null ? new Date(todoDTO.getDueDate()) : null;
        
        String category = todoModel.predictClass(todoDTO.getTitle());
        
        Todo todo = new Todo(todoDTO.getTitle(), todoDTO.getDescription(), todoDTO.isFinished(), assigneeList, new Date(), dueDate, null, category);
        todoRepository.save(todo);
        PostTodoDTO response = new PostTodoDTO(todo.getId(), todoDTO.getTitle(), todoDTO.getDescription(), todoDTO.isFinished(), assigneeList, new Date().getTime(), dueDate.getTime(), todo.getCategory());
        return response;
    }

    /**
     * Retrieves all Todos from the repository.
     * Each Todo is converted to a GetTodoDTO with dates as Unix timestamp and returned in a list
     *
     * @return A list of GetTodoDTO objects representing all Todo items.
     */
    @GetMapping("/todos")
    public List<GetTodoDTO> getAllTodos() {
        List<Todo> todos = todoRepository.findAll();
        List<GetTodoDTO> todoDTOs = new ArrayList<>();

        for (Todo todo : todos) {
            GetTodoDTO dto = new GetTodoDTO();
            dto.setId(todo.getId());
            dto.setTitle(todo.getTitle());
            dto.setDescription(todo.getDescription());
            dto.setFinished(todo.isFinished());
            dto.setAssigneeList(todo.getAssigneeList());
            dto.setCreatedDate(todo.getCreatedDate().getTime());
            dto.setDueDate(todo.getDueDate() != null ? todo.getDueDate().getTime() : null);
            dto.setFinishedDate(todo.getFinishedDate() != null ? todo.getFinishedDate().getTime() : null);
            dto.setCategory(todo.getCategory());
            todoDTOs.add(dto);
        }
        return todoDTOs;
    }

    /**
     * Retrieves a specific Todo by its ID.
     * If the Todo is found, it is returned as a GetTodoDTO
     * else a ResponseStatusException with a NOT_FOUND status is thrown
     *
     * @param id The ID of the Todo to retrieve.
     * @return A GetTodoDTO representing the requested Todo.
     */
    @GetMapping("/todos/{id}")
    public GetTodoDTO getTodoById(@PathVariable Long id) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        GetTodoDTO dto = new GetTodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setFinished(todo.isFinished());
        dto.setAssigneeList(todo.getAssigneeList());
        dto.setCreatedDate(todo.getCreatedDate().getTime());
        dto.setDueDate(todo.getDueDate() != null ? todo.getDueDate().getTime() : null);
        dto.setFinishedDate(todo.getFinishedDate() != null ? todo.getFinishedDate().getTime() : null);
        dto.setCategory(todo.getCategory());
        return dto;
    }

    /**
     * Updates an existing Todo identified by its ID
     * The provided TodoDTO is used to update the Todo's attributes
     * Assignee IDs are validated and converted. If the Todo is found, it is updated and saved
     * The updated Todo is then returned as a GetTodoDTO
     *
     * @param id The ID of the Todo to update
     * @param todoDTO The TodoDTO containing the updated Todo data
     * @return A GetTodoDTO representing the updated Todo item
     */
    @PutMapping("/todos/{id}")
    public GetTodoDTO updateTodo(@PathVariable Long id, @Valid @RequestBody TodoDTO todoDTO) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        
        List<Assignee> assigneeList = convertDtoToAssigneeList(todoDTO.getAssigneeIdList());
        
        Date dueDate = todoDTO.getDueDate() != null ? new Date(todoDTO.getDueDate()) : null;
        
        String category = todoModel.predictClass(todoDTO.getTitle());
        
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setFinished(todoDTO.isFinished());
        todo.setAssigneList(assigneeList);
        todo.setDueDate(dueDate);
        todo.setCategory(category);

        todoRepository.save(todo);

        GetTodoDTO dto = new GetTodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setFinished(todo.isFinished());
        dto.setAssigneeList(assigneeList);
        dto.setCreatedDate(todo.getCreatedDate().getTime());
        dto.setDueDate(todo.getDueDate() != null ? todo.getDueDate().getTime() : null);
        dto.setCategory(todo.getCategory());
        return dto;
    }

    /**
     * Deletes a Todo identified by its ID
     * If the Todo is found, it is deleted from the repository
     * If not found, a ResponseStatusException with a NOT_FOUND status is thrown
     *
     * @param id The ID of the Todo to delete
     */
    @DeleteMapping("/todos/{id}")
    public void deleteTodo(@PathVariable Long id) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        todoRepository.deleteById(id);
    }

    /**
     * Converts a list of assignee IDs into a list of Assignee entities
     * Each ID is validated to ensure the corresponding Assignee exists
     * If any Assignee ID is invalid, a ResponseStatusException with a BAD_REQUEST status is thrown
     *
     * @param assigneeIdList A list of assignee IDs to convert.
     * @return A list of Assignee entities corresponding to the provided IDs.
     */
    private List<Assignee> convertDtoToAssigneeList(List<Long> assigneeIdList) {
        List<Assignee> assignees = new ArrayList<>();
        if (assigneeIdList != null) {
            for (Long assigneeId : assigneeIdList) {
                Assignee assignee = assigneeRepository.findById(assigneeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee not found"));
                assignees.add(assignee);
            }
        }
        return assignees;
    }
    
}
