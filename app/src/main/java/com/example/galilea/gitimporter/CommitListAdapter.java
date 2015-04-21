package com.example.galilea.gitimporter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.galilea.gitimporter.processing.CommitPOJOContainer;

import java.util.List;

/**
 * Created by galilea on 17.02.2015.
 */

public class CommitListAdapter extends RecyclerView.Adapter<CommitListAdapter.ViewHolder> {

    private List<SimpleCommit> items;


    public CommitListAdapter(List<SimpleCommit> newItems){
        items = newItems;
    }

    @Override
    public CommitListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommitListAdapter.ViewHolder holder, int position) {

        SimpleCommit i = items.get(position);

        holder.setName(i.author);
        holder.setMessage(i.message);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView message;
        protected View item;

        public TextView getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message.setText(message);
        }

        public View getItem() {
            return item;
        }

        public void setItem(View item) {
            this.item = item;
        }

        public TextView getName() {
            return name;
        }

        public void setName(String name) {

            this.name.setText(name);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            name =  (TextView)itemView.findViewById(R.id.name_text);
            message = (TextView)itemView.findViewById(R.id.message_text);

        }


    }

}
