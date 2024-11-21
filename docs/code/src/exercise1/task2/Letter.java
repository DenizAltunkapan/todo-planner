package exercise1.task2;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "letters")
public class Letter {

    @DatabaseField(id = true)
    private Integer id;

    @DatabaseField(columnName = "letter")
    private String letter;

    public Letter() {}

    public String getLetter() {
        return letter;
    }

    public Integer getId() {
        return id;
    }
}
