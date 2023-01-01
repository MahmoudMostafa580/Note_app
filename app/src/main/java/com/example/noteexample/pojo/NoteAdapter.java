package com.example.noteexample.pojo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteexample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    List<Note> mList = new ArrayList<>();
    public OnItemClickListener mListener;

    /*public NoteAdapter() {
        super(DIFF_CALLBACK);
    }
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };*/

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note currentNote = mList.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getContent());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setNotes(List<Note> notesList) {
        mList = notesList;
        notifyDataSetChanged();
    }

    public void swapItems(List<Note> notes){
        final NotesDiffCallback diffCallback= new NotesDiffCallback(this.mList, notes);
        final DiffUtil.DiffResult diffResult= DiffUtil.calculateDiff(diffCallback);

        this.mList.clear();
        this.mList.addAll(notes);
        diffResult.dispatchUpdatesTo(this);
    }

    public Note getNoteAt(int position) {
        return mList.get(position);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle, textViewDescription, textViewPriority;

        public NoteViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.title_tv);
            textViewDescription = itemView.findViewById(R.id.description_tv);
            textViewPriority = itemView.findViewById(R.id.priority_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(mList.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class NotesDiffCallback extends DiffUtil.Callback {

        private List<Note> mOldList;
        private List<Note> mNewList;

        public NotesDiffCallback(List<Note> mOldList, List<Note> mNewList) {
            this.mOldList = mOldList;
            this.mNewList = mNewList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).getId() == mNewList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Note oldNote = mOldList.get(oldItemPosition);
            Note newNote = mNewList.get(newItemPosition);

            return Objects.equals(oldNote.getTitle(), newNote.getTitle())
                    && Objects.equals(oldNote.getContent(), newNote.getContent())
                    && oldNote.getPriority() == newNote.getPriority();
        }
    }
}
