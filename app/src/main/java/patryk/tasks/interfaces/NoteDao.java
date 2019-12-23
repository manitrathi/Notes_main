package patryk.tasks.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import patryk.tasks.models.Note;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM notes_table")
    void deleteAll();

    @Query("SELECT * FROM notes_table ORDER BY priority DESC")
    LiveData<List<Note>> getNotesByPriority();

    @Query("SELECT * FROM notes_table ORDER BY lower(note) ASC")
    LiveData<List<Note>> getNotesByName();

    @Query("SELECT * FROM notes_table ORDER BY date ASC")
    LiveData<List<Note>> getNotesByDate();
}
