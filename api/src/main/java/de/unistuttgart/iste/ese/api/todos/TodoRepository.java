package de.unistuttgart.iste.ese.api.todos;

import de.unistuttgart.iste.ese.api.assignees.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByAssigneeList(Assignee assignee);
}
