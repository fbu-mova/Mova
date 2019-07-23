package com.example.mova.fragments.Personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.ComponentAdapter;
import com.example.mova.components.ComponentLayout;
import com.example.mova.components.JournalPromptComponent;
import com.example.mova.components.TomorrowFocusPromptComponent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFeedFragment extends Fragment {

    @BindView(R.id.component) protected ComponentLayout container;
    @BindView(R.id.rvCards)   protected RecyclerView rvCards;

    public PersonalFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PersonalFeedFragment.
     */
    public static PersonalFeedFragment newInstance() {
        PersonalFeedFragment fragment = new PersonalFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        // insertSoloComponent(false);
        // TODO: Set up recyclerview
        rvCards.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void insertSoloComponent(boolean toggleJournalVsTomorrow) {
        if (toggleJournalVsTomorrow) {
            JournalPromptComponent card = new JournalPromptComponent();
            container.inflateComponent((DelegatedResultActivity) getActivity(), card);
        } else {
            TomorrowFocusPromptComponent card = new TomorrowFocusPromptComponent() {
                @Override
                public void onLoadGoals(Throwable e) {
                    container.inflateComponent((DelegatedResultActivity) getActivity(), this);
                }
            };
        }
    }
}
