package de.unistuttgart.iste.ese.api.todos;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import de.unistuttgart.iste.ese.api.todos.dtos.GetTodoDTO;
import de.unistuttgart.iste.ese.api.todos.dtos.PostTodoDTO;
import de.unistuttgart.iste.ese.api.todos.dtos.TodoDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Handles CRUD operations for todos.
 * Each endpoint delegates the actual business logic to the TodoService.
 */
@RestController
@ApiVersion1
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * Creates a new Todo by invoking the createTodo method in the TodoService
     *
     * @param todoDTO The data transfer object containing the information for the new Todo.
     * @return A PostTodoDTO with the details of the newly created Todo.
     */
    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public PostTodoDTO createTodo(@Valid @RequestBody TodoDTO todoDTO) {
        return todoService.createTodo(todoDTO);
    }

    /**
     * Retrieves all Todos by calling the getAllTodos method from the TodoService
     *
     * @return A list of GetTodoDTO objects representing all the Todos in the system.
     */
    @GetMapping("/todos")
    public List<GetTodoDTO> getAllTodos() {
        return todoService.getAllTodos();
    }

    /**
     * Fetches a Todo by its unique ID by invoking the getTodoById method in the TodoService.
     * If the Todo exists, it is returned as a GetTodoDTO. Otherwise, a 404 error is thrown.
     *
     * @param id The ID of the Todo to retrieve.
     * @return A GetTodoDTO representing the Todo with the given ID.
     * @throws ResponseStatusException If no Todo with the given ID is found.
     */
    @GetMapping("/todos/{id}")
    public GetTodoDTO getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id);
    }

    /**
     * Updates an existing Todo identified by its ID by calling the updateTodo method in the TodoService.
     * If the Todo exists, it is updated and returned as a GetTodoDTO.
     * If the Todo cannot be found, a 404 error is thrown.
     *
     * @param id The ID of the Todo to update.
     * @param todoDTO The updated Todo data.
     * @return A GetTodoDTO representing the updated Todo.
     * @throws ResponseStatusException If no Todo with the given ID is found.
     */
    @PutMapping("/todos/{id}")
    public GetTodoDTO updateTodo(@PathVariable Long id, @Valid @RequestBody TodoDTO todoDTO) {
        return todoService.updateTodo(id, todoDTO);
    }

    /**
     * Deletes a Todo identified by its ID by invoking the deleteTodoById method in the TodoService.
     * If the Todo exists, it will be deleted from the system. If not, a 404 error is thrown.
     *
     * @param id The ID of the Todo to delete.
     * @throws ResponseStatusException If no Todo with the given ID is found.
     */
    @DeleteMapping("/todos/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodoById(id);
    }

    /**
     * Exports all Todos to a CSV file by calling the exportTodosToCSV method in the TodoService.
     * The file will be downloaded with the name "todos.csv".
     *
     * @param response The HttpServletResponse to write the CSV data to.
     * @throws IOException If an error occurs while generating or streaming the CSV file.
     */
    @GetMapping(value = "/csv-downloads/todos", produces = "text/csv")
    public void exportTodosToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"todos.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            todoService.exportTodosToCSV(writer);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating CSV file", e);
        }
    }

}