package patryk.tasks.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notes_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String note;

    private Date date;

    private int priority;

    public Note(String note, Date date, int priority) {
        this.note = note;
        this.date = date;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public Date getDate() {
        return date;
    }

    public int getPriority() {
        return priority;
    }
}
