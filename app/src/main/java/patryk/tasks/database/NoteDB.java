package patryk.tasks.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import patryk.tasks.activities.MainActivity;
import patryk.tasks.converters.Converters;
import patryk.tasks.interfaces.NoteDao;
import patryk.tasks.models.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class NoteDB extends RoomDatabase {

    private static NoteDB instance;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDatabaseAsyncTask(instance).execute();
        }
    };

    public static synchronized NoteDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDB.class, "NoteDB")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract NoteDao noteDao();

    /*
     * Backup method
     * Handles situation when user updates app to new version
     * And has some data stored in old storage file
     * We are reading data from text file
     * And adding all notes which was saved in that file to the database
     */
    private static class PopulateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {

        NoteDao noteDao;

        private PopulateDatabaseAsyncTask(NoteDB db) {
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            File filesDir = MainActivity.fileDir;
            File todoFile = new File(filesDir, "todo.txt");

            if (todoFile.exists()) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(todoFile);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        noteDao.insert(new Note(line, new Date(), 1));
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Delete old txt file
            todoFile.delete();
            return null;
        }
    }
}
