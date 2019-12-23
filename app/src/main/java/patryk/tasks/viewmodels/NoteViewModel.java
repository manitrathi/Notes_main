package patryk.tasks.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import patryk.tasks.models.Note;
import patryk.tasks.repository.NoteRepository;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private MediatorLiveData<List<Note>> observableNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);

        // List that can change due to sorting
        observableNotes = new MediatorLiveData<>();

        // Get notes LiveData
        LiveData<List<Note>> notes = repository.getNotes();

        // Whenever notes LiveData changes our note list gets updated
        observableNotes.addSource(notes, observableNotes::setValue);
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<Note>> getNotes() {
        return observableNotes;
    }

    public void sortNotes(String orderBy) {
        repository.setSortingOrder(orderBy);
    }
}
