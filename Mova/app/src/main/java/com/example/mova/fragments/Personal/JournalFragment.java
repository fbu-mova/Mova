package com.example.mova.fragments.Personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.containers.GestureLayout;
import com.example.mova.containers.GestureListener;
import com.example.mova.dialogs.ComposePostDialog;
import com.example.mova.utils.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.SortedDataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.DatePickerComponent;
import com.example.mova.components.JournalEntryComponent;
import com.example.mova.model.Journal;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.containers.EndlessScrollLayout;
import com.example.mova.containers.EndlessScrollRefreshLayout;
import com.example.mova.containers.ScrollLoadHandler;
import com.example.mova.utils.DataEvent;
import com.example.mova.utils.TimeUtils;
import com.example.mova.activities.JournalComposeActivity;
import com.example.mova.model.Post;
import com.example.mova.views.EdgeFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalFragment extends Fragment {

    private SortedDataComponentAdapter<Date> dateAdapter;
    private SortedDataComponentAdapter<Post> entryAdapter;

    private Journal journal;
    private Date currDate;

    private GestureDetector gestureDetector;
    private DataEvent<Date> dateSelectEvent;

    @BindView(R.id.glRoot)      protected GestureLayout glRoot;
    @BindView(R.id.tvTitle)     protected TextView tvTitle;
    @BindView(R.id.tvDate)      protected TextView tvDate;
    @BindView(R.id.eslDates)    protected EndlessScrollLayout<Component.ViewHolder> eslDates;
    @BindView(R.id.esrlEntries) protected EndlessScrollRefreshLayout<Component.ViewHolder> esrlEntries;
    @BindView(R.id.efabCompose)  protected EdgeFloatingActionButton efabCompose;

    public JournalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment JournalFragment.
     */
    public static JournalFragment newInstance() {
        JournalFragment fragment = new JournalFragment();
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
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        currDate = TimeUtils.getToday();
        dateSelectEvent = new DataEvent<>();

        configureGestureHandling();

        journal = new Journal(
            User.getCurrentUser(),
            new SortedList.Callback<Date>() {
                @Override
                public int compare(Date o1, Date o2) {
                    return Journal.defaultCompareDates(o1, o2);
                }

                @Override
                public void onChanged(int position, int count) {
                    dateAdapter.notifyItemRangeChanged(position, count);
                }

                @Override
                public boolean areContentsTheSame(Date oldItem, Date newItem) {
                    return Journal.defaultDatesEqual(oldItem, newItem);
                }

                @Override
                public boolean areItemsTheSame(Date item1, Date item2) {
                    return Journal.defaultDatesEqual(item1, item2);
                }

                @Override
                public void onInserted(int position, int count) {
                    dateAdapter.notifyItemRangeInserted(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    dateAdapter.notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    dateAdapter.notifyItemMoved(fromPosition, toPosition);
                }
            },
            Journal.makeDefaultPostSortHandler()
        );

        // On date click, display only the entries for that date
        dateAdapter = new SortedDataComponentAdapter<Date>((DelegatedResultActivity) getActivity(), journal.getDates()) {
            @Override
            public Component makeComponent(Date item, Component.ViewHolder holder) {
                return new DatePickerComponent(item, dateSelectEvent, (view, date) -> {
                    currDate = date;
                    displayEntries(currDate);
                });
            }

            @Override
            protected Component.Inflater makeInflater(Date item) {
                return new DatePickerComponent.Inflater();
            }
        };

        entryAdapter = new SortedDataComponentAdapter<Post>((DelegatedResultActivity) getActivity(), journal.getEntriesByDate(currDate)) {
            @Override
            public Component makeComponent(Post item, Component.ViewHolder holder) {
                return new JournalEntryComponent(item);
            }

            @Override
            protected Component.Inflater makeInflater(Post item) {
                return new JournalEntryComponent.Inflater();
            }
        };

        LinearLayoutManager dateLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        LinearLayoutManager entryLayoutManager = new LinearLayoutManager(getActivity());

        eslDates.init(
            new EndlessScrollLayout.LayoutConfig()
                    .setHeightSize(EndlessScrollLayout.LayoutConfig.Size.wrap_content)
                    .setOrientation(EndlessScrollLayout.LayoutConfig.Orientation.Horizontal),
            new ScrollLoadHandler<Component.ViewHolder>() {
                @Override
                public void load() {
                    loadEntries();
                }

                @Override
                public void loadMore() {
                    loadMoreEntries();
                }

                @Override
                public SortedDataComponentAdapter<Date> getAdapter() {
                    return dateAdapter;
                }

                @Override
                public RecyclerView.LayoutManager getLayoutManager() {
                    return dateLayoutManager;
                }

                @Override
                public int[] getColorScheme() {
                    return ScrollLoadHandler.getDefaultColorScheme();
                }
             }
        );

        esrlEntries.init(
            new EndlessScrollRefreshLayout.LayoutConfig(),
            new ScrollLoadHandler<Component.ViewHolder>() {
                // TODO: Perhaps only load more entries for that specific date?
                @Override
                public void load() {
                    loadEntries();
                }

                @Override
                public void loadMore() {
                    loadMoreEntries();
                }

                @Override
                public SortedDataComponentAdapter<Post> getAdapter() {
                    return entryAdapter;
                }

                @Override
                public RecyclerView.LayoutManager getLayoutManager() {
                    return entryLayoutManager;
                }

                @Override
                public int[] getColorScheme() {
                    return getDefaultColorScheme();
                }
            }
        );

        int elementMargin = (int) getResources().getDimension(R.dimen.elementMargin);
        eslDates.addItemDecoration(new EdgeDecorator(new EdgeDecorator.Config(elementMargin, 0, 0, 0)
                .setOrientation(EdgeDecorator.Orientation.Horizontal)
                .setStart(EdgeDecorator.Start.Reverse)));
        esrlEntries.addItemDecoration(new EdgeDecorator(0, 0, 0, 64));

        // On fab click, open compose activity
        efabCompose.setOnClickListener((clickedView) -> {
            PostConfig config = new PostConfig();
            config.isPersonal = true;
            config.displayMoodSelector = true;

            ComposePostDialog dialog = new ComposePostDialog((DelegatedResultActivity) getActivity(), config) {
                @Override
                protected void onCancel() {

                }

                @Override
                protected void onPost(PostConfig config) {
                    config.savePost((entry) -> {
                        journal.addEntry(TimeUtils.normalizeToDay(config.post.getCreatedAt()), config.post);
                        displayEntries(TimeUtils.normalizeToDay(new Date()));
                    });
                }
            };

            dialog.show();
        });

        displayEntries(currDate);
        loadEntries();
    }

    private void displayEntries(Date date) {
        // Change the displayed date in the date picker and the subheader text
        tvDate.setText(TimeUtils.toDateString(date));
        dateSelectEvent.fire(date);

        // Change the source of the entries--important to reattach the adapter each time the source is changed
        SortedList<Post> entriesFromDate = journal.getEntriesByDate(date);
        entryAdapter.changeSource(entriesFromDate);
        esrlEntries.reattachAdapter();
        entryAdapter.notifyDataSetChanged();
    }

    private void loadEntries() {
        journal.loadEntries((e) -> {
            displayEntries(currDate);
            esrlEntries.setRefreshing(false);
        });
    }

    private void loadMoreEntries() {
        journal.loadMoreEntries((e) -> {
            // If the current date is not yet added, add it
            Date today = TimeUtils.normalizeToDay(new Date());
            if (journal.getDates().indexOf(today) < 0) journal.addEmptyDate(today);

            displayEntries(currDate);
            esrlEntries.setRefreshing(false);
        });
    }

    private void configureGestureHandling() {
        gestureDetector = new GestureDetector(getActivity(), new GestureListener(glRoot) {
            @Override
            public boolean onTouch() {
                return false;
            }

            @Override
            public boolean onSwipe(List<Direction> directions) {
                Date date = currDate;

                if (directions.indexOf(Direction.Left) >= 0) {
                    date = journal.getPrevDate(currDate);
                } else if (directions.indexOf(Direction.Right) >= 0) {
                    date = journal.getNextDate(currDate);
                }

                if (date == currDate) return false;

                currDate = date;
                displayEntries(currDate);
                return false;
            }
        });

        glRoot.setOnTouchListener((View v, MotionEvent event) -> !gestureDetector.onTouchEvent(event));
        glRoot.setGestureDetector(gestureDetector);
    }
}
