package com.example.mova.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.model.Action;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActionsAdapter extends RecyclerView.Adapter<ComposeActionsAdapter.ViewHolder> {

    private List<Action> actions;
    Context context;

    public ComposeActionsAdapter(List<Action> actions) {
        this.actions = actions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View actionView = inflater.inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(actionView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComposeActionsAdapter.ViewHolder holder, int position) {
        Action action = actions.get(position);

        // fixme -- needs to be actions to get the icon logic, or needs to get those info somewhere
            // fixme -- also, how would UI look for recurring / reminder?

        holder.cbItem.setText(action.getTask());

        int visible = (action.getIsPriority()) ? View.VISIBLE : View.GONE;
        holder.ivPriority.setVisibility(visible);

    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cbItem)      public CheckBox cbItem;
        @BindView(R.id.ivPriority)  public ImageView ivPriority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

//            itemView.setOnClickListener(this); // fixme: user can tap anywhere on recyclerview and dialog will show up to compose action

            // todo -- set onclicklistener so can edit if haven't submitted yet / dialog reshows up to edit
        }

    }

}
