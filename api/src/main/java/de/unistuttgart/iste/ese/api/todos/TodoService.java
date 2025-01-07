package de.unistuttgart.iste.ese.api.todos;

import de.unistuttgart.iste.ese.api.assignees.Assignee;
import de.unistuttgart.iste.ese.api.assignees.AssigneeRepository;
import de.unistuttgart.iste.ese.api.todos.dtos.GetTodoDTO;
import de.unistuttgart.iste.ese.api.todos.dtos.PostTodoDTO;
import de.unistuttgart.iste.ese.api.todos.dtos.TodoDTO;
import jakarta.validation.Valid;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling business logic related to Todos.
 * Provides CRUD - methods and exporting Todos.
 */
@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private AssigneeRepository assigneeRepository;

    private final TodoModel todoModel = new TodoModel("model.pmml");

    /**
     * Creates a new Todo based on the provided TodoDTO.
     * Validates assignee IDs and determines the category of the Todo.
     *
     * @param todoDTO The data for the new Todo submitted via the request body.
     * @return A DTO representing the created Todo.
     */
    public PostTodoDTO createTodo(@Valid TodoDTO todoDTO) {
        List<Assignee> assigneeList = convertDtoToAssigneeList(todoDTO.getAssigneeIdList());
        Date dueDate = todoDTO.getDueDate() != null ? new Date(todoDTO.getDueDate()) : null;
        String category = todoModel.predictClass(todoDTO.getTitle());

        Todo todo = new Todo(todoDTO.getTitle(), todoDTO.getDescription(), todoDTO.isFinished(), assigneeList, new Date(), dueDate, null, category);
        todoRepository.save(todo);

        return new PostTodoDTO(todo.getId(), todo.getTitle(), todo.getDescription(), todo.isFinished(),
            assigneeList, todo.getCreatedDate().getTime(), dueDate != null ? dueDate.getTime() : null, category);
    }

    /**
     * Retrieves all Todos from the repository.
     *
     * @return A list of Todos as GetTodoDTO objects.
     */
    public List<GetTodoDTO> getAllTodos() {
        return todoRepository.findAll().stream().map(this::mapToGetTodoDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a Todo by its unique ID.
     * Throws an exception if the Todo is not found.
     *
     * @param id The ID of the Todo to retrieve.
     * @return The requested Todo as a GetTodoDTO.
     * @throws ResponseStatusException If the Todo is not found.
     */
    public GetTodoDTO getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        return mapToGetTodoDTO(todo);
    }

    /**
     * Updates an existing Todo identified by its ID.
     * Validates assignee IDs, updates the attributes, and saves the updated Todo.
     *
     * @param id The ID of the Todo to update.
     * @param todoDTO The updated Todo data.
     * @return The updated Todo as a GetTodoDTO.
     * @throws ResponseStatusException If the Todo is not found.
     */
    public GetTodoDTO updateTodo(Long id, @Valid TodoDTO todoDTO) {
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
        return mapToGetTodoDTO(todo);
    }

    /**
     * Deletes a Todo by its unique ID.
     * Throws an exception if the Todo is not found.
     *
     * @param id The ID of the Todo to delete.
     * @throws ResponseStatusException If the Todo is not found.
     */
    public void deleteTodoById(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found");
        }
        todoRepository.deleteById(id);
    }

    /**
     * Converts a list of assignee IDs into a list of Assignee entities.
     * Throws an exception if any assignee ID is invalid.
     *
     * @param assigneeIdList A list of assignee IDs to convert.
     * @return A list of Assignee entities.
     * @throws ResponseStatusException If an assignee is not found.
     */
    private List<Assignee> convertDtoToAssigneeList(List<Long> assigneeIdList) {
        if (assigneeIdList == null || assigneeIdList.isEmpty()) {
            return new ArrayList<>();
        }

        return assigneeIdList.stream()
            .map(id -> assigneeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee not found")))
            .collect(Collectors.toList());
    }

    /**
     * Maps a Todo entity to a GetTodoDTO.
     *
     * @param todo The Todo entity to map.
     * @return A GetTodoDTO representing the Todo.
     */
    private GetTodoDTO mapToGetTodoDTO(Todo todo) {
        return new GetTodoDTO(
            todo.getId(),
            todo.getTitle(),
            todo.getDescription(),
            todo.isFinished(),
            todo.getAssigneeList(),
            todo.getCreatedDate().getTime(),
            todo.getDueDate() != null ? todo.getDueDate().getTime() : null,
            todo.getFinishedDate() != null ? todo.getFinishedDate().getTime() : null,
            todo.getCategory()
        );
    }

    /**
     * Exports all Todos as a CSV file.
     *
     * @param writer The PrintWriter to write the CSV data to.
     * @throws IOException If an error occurs during CSV generation.
     */
    public void exportTodosToCSV(Writer writer) throws IOException {
        List<Todo> todos = todoRepository.findAll();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        try (CSVPrinter csvPrinter = new CSVPrinter(writer,
            CSVFormat.DEFAULT.withHeader("id", "title", "description", "finished", "assignees", "createdDate", "dueDate", "finishedDate", "category"))) {
            for (Todo todo : todos) {
                csvPrinter.printRecord(mapTodoToCsvRecord(todo, dateFormatter));
            }
        }
    }


    /**
     * Maps a Todo entity to a CSV record.
     *
     * @param todo The Todo entity to map.
     * @param dateFormatter A SimpleDateFormat instance for formatting dates.
     * @return A list of strings representing the CSV record.
     */
    private List<String> mapTodoToCsvRecord(Todo todo, SimpleDateFormat dateFormatter) {
        String assignees = todo.getAssigneeList().stream()
            .map(a -> a.getPrename() + " " + a.getName())
            .collect(Collectors.joining("+"));

        return List.of(
            String.valueOf(todo.getId()),
            todo.getTitle(),
            todo.getDescription(),
            String.valueOf(todo.isFinished()),
            assignees,
            formatOrEmpty(todo.getCreatedDate(), dateFormatter),
            formatOrEmpty(todo.getDueDate(), dateFormatter),
            formatOrEmpty(todo.getFinishedDate(), dateFormatter),
            todo.getCategory()
        );
    }

    /**
     * Formats a date or returns an empty string if the date is null.
     *
     * @param date The date to format.
     * @param formatter The formatter to use.
     * @return A formatted date string or an empty string if the date is null.
     */
    private String formatOrEmpty(Date date, SimpleDateFormat formatter) {
        return date != null ? formatter.format(date) : "";
    }
    
}
