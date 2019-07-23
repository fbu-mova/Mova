package com.example.mova.fragments.Personal;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mova.R;
import com.example.mova.activities.GoalComposeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActionDialog extends DialogFragment implements TextView.OnEditorActionListener {

    @BindView(R.id.etAction)        protected EditText etAction;
    @BindView(R.id.btSubmitAction)  protected Button btSubmitAction;

    public ComposeActionDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    // never really use?
    public static ComposeActionDialog newInstance(String title) {
        ComposeActionDialog frag = new ComposeActionDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_compose_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        btSubmitAction.setOnEditorActionListener(this);

//        btSubmitAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String action = etAction.getText().toString();
//            }
//        });
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface SubmitActionDialogListener {
        void onFinishActionDialog(String inputText);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            SubmitActionDialogListener listener = (SubmitActionDialogListener) getActivity();
            
            // todo -- add ability to pass other data, e.g. icons/notifications info
            listener.onFinishActionDialog(etAction.getText().toString());
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;

    }
}
