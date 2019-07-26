package com.example.mova.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActionsAdapter extends RecyclerView.Adapter<ComposeActionsAdapter.ViewHolder> {

    // no longer used

    private List<String> actions;
    Context context;

    public ComposeActionsAdapter(List<String> actions) {
        this.actions = actions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View actionView = inflater.inflate(R.layout.item_compose_action, parent, false);
        return new ViewHolder(actionView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComposeActionsAdapter.ViewHolder holder, int position) {
        String action = actions.get(position);

        holder.rbAction.setText(action);

    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

//    @Override
//    public void onClick(View v) {
//        showAlertDialog();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rbAction)    public RadioButton rbAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

//            itemView.setOnClickListener(this); // fixme: user can tap anywhere on recyclerview and dialog will show up to compose action

            // todo -- set onclicklistener so can edit if haven't submitted yet / dialog reshows up to edit
        }

    }

}