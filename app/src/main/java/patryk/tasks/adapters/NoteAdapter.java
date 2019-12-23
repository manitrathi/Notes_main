package patryk.tasks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import patryk.tasks.R;
import patryk.tasks.models.Note;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getNote().equals(newItem.getNote()) &&
                    oldItem.getDate().equals(newItem.getDate()) &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };
    private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private OnItemClickListener mListener;
    private Context context;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {

        Note note = getItem(position);

        holder.note.setText(note.getNote());
        holder.date.setText(dateFormat.format(note.getDate()));
        holder.priority.setText(String.format(context.getResources().getString(R.string.adapter_priority_formatted_string), note.getPriority()));
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView note;
        private TextView date;
        private TextView priority;

        private NoteHolder(@NonNull View itemView) {
            super(itemView);

            note = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);
            priority = itemView.findViewById(R.id.priority);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(getItem(position));
                }
            }
        }
    }
}
