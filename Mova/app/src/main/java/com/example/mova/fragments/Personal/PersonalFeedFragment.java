package com.example.mova.fragments.Personal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.PrioritizedComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.components.JournalPromptComponent;
import com.example.mova.components.TomorrowFocusPromptComponent;
import com.example.mova.feed.PersonalFeedPrioritizer;
import com.example.mova.feed.PrioritizedComponent;
import com.example.mova.scrolling.EdgeDecorator;

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

    private PrioritizedComponentAdapter adapter;
    private SortedList<PrioritizedComponent> cards;
    private PersonalFeedPrioritizer prioritizer;

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

//        insertSoloComponent(false);

        cards = new SortedList<>(PrioritizedComponent.class, new SortedList.Callback<PrioritizedComponent>() {
            @Override
            public int compare(PrioritizedComponent o1, PrioritizedComponent o2) {
                return o2.compareTo(o1); // Higher values come first
            }

            @Override
            public void onChanged(int position, int count) {
                adapter.notifyItemChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(PrioritizedComponent oldItem, PrioritizedComponent newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(PrioritizedComponent item1, PrioritizedComponent item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
                adapter.notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                adapter.notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                adapter.notifyItemMoved(fromPosition, toPosition);
            }
        });

        adapter = new PrioritizedComponentAdapter((DelegatedResultActivity) getActivity(), cards);
        rvCards.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCards.setAdapter(adapter);
        rvCards.addItemDecoration(new EdgeDecorator(32));

        prioritizer = new PersonalFeedPrioritizer();
        prioritizer.makeCards(cards, (e) -> {
            if (e != null) {
                Log.e("PersonalFeedFragment", "Failed to make cards", e);
                Toast.makeText(getActivity(), "Failed to load feed", Toast.LENGTH_LONG).show();
            } else {
                Log.i("PersonalFeedFragment", "Loaded cards successfully!");
            }
        });
    }

    private void insertSoloComponent(boolean toggleJournalVsTomorrow) {
        if (toggleJournalVsTomorrow) {
            JournalPromptComponent card = new JournalPromptComponent();
            container.inflateComponent((DelegatedResultActivity) getActivity(), card);
        } else {
            TomorrowFocusPromptComponent card = new TomorrowFocusPromptComponent(0, 5) {

                @Override
                public void onLoadGoals(Throwable e) {
                    if (e == null) {
                        container.inflateComponent((DelegatedResultActivity) getActivity(), this);
                    }
                }
            };
        }
    }
}
