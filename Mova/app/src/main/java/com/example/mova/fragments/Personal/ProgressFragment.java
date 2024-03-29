package com.example.mova.fragments.Personal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.adapters.ViewAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.ProgressGoalComponent;
import com.example.mova.components.ProgressGridMoodComponent;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.containers.ProgressStackManager;
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.model.Goal;
import com.example.mova.model.Journal;
import com.example.mova.model.Mood;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.TimeUtils;
import com.example.mova.views.ProgressStack;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    //Todo- allow length to be changed

    @BindView(R.id.tvY1)    protected TextView tvY1;
    @BindView(R.id.tvY2)    protected TextView tvY2;
    @BindView(R.id.rvMood)  protected RecyclerView rvMood;

    @BindView(R.id.rvWell)  protected RecyclerView rvWell;
    @BindView(R.id.rvWork)  protected RecyclerView rvWork;

    @BindView(R.id.tvGoingWell) protected TextView tvWell;
    @BindView(R.id.tvNeedsWork) protected TextView tvWork;

    //Progress Stacks
    private List<ProgressStack> progressStacks;
    @BindView(R.id.progressStack1) protected ProgressStack ps1;
    @BindView(R.id.progressStack2) protected ProgressStack ps2;
    @BindView(R.id.progressStack3) protected ProgressStack ps3;
    @BindView(R.id.progressStack4) protected ProgressStack ps4;
    @BindView(R.id.progressStack5) protected ProgressStack ps5;
    @BindView(R.id.progressStack6) protected ProgressStack ps6;
    @BindView(R.id.progressStack7) protected ProgressStack ps7;

    private List<Goal> mGoals;
    private List<Goal> graphGoals;
    private List<Goal> goodGoals;
    private List<Goal> badGoals;
    private List<MoodWrapper> userMoods;

    //Hue Used Lists
    private List<ColorUtils.Lightness> hueBlue;
    private List<ColorUtils.Lightness> huePurple;
    private List<ColorUtils.Lightness> hueOrange;

    private ProgressStackManager graphManager;
    private HashMap<Goal, Integer> hueMap;
    private HashMap<Integer, Goal> colorGoals;
    private List<Integer> usedColors;
    private List<Integer> lastShownColors;

    private int length = 0;
    private User user;
    private ViewAdapter<ProgressStack> graphAdapter;
    private DataComponentAdapter<MoodWrapper> gridMoodAdapter;
    private DataComponentAdapter<Goal> goalsWellAdapter;
    private DataComponentAdapter<Goal> goalsWorkAdapter;

    private boolean inSelectAnimation;
    private boolean hasDisplayedMood;

    private Journal journal;

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProgressFragment.
     */

    public static ProgressFragment newInstance() {
        ProgressFragment fragment = new ProgressFragment();
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
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        progressStacks = new ArrayList<>();
        progressStacks.add(ps1);
        progressStacks.add(ps2);
        progressStacks.add(ps3);
        progressStacks.add(ps4);
        progressStacks.add(ps5);
        progressStacks.add(ps6);
        progressStacks.add(ps7);

        user = User.getCurrentUser();
        length = 7;

        hueBlue = new ArrayList<>();
        hueOrange = new ArrayList<>();
        huePurple = new ArrayList<>();

        hueMap = new HashMap<>();
        usedColors = new ArrayList<>();
        mGoals = new ArrayList<>();
        graphGoals = new ArrayList<>();
        colorGoals = new HashMap<>();
        lastShownColors = new ArrayList<>();
        userMoods = new ArrayList<>();
        goodGoals = new ArrayList<>();
        badGoals = new ArrayList<>();
        graphManager = new ProgressStackManager(getActivity(), progressStacks);

        journal = new Journal(User.getCurrentUser());

        configureGraphClick();

        // Create adapters

        gridMoodAdapter = new DataComponentAdapter<MoodWrapper>((DelegatedResultActivity) getActivity(), userMoods) {
            @Override
            public Component makeComponent(MoodWrapper item, Component.ViewHolder holder) {
                return new ProgressGridMoodComponent(item.mood, item.date) {
                    @Override
                    public void onClick() {
                        BottomNavigationView menu = getActivity().findViewById(R.id.bottom_navigation_personal);
                        PersonalFragment.journalDate = item.date;
                        menu.setSelectedItemId(R.id.action_journal);

//                        Fragment frag = JournalFragment.newInstance(item.date);
//                        FragmentManager manager = ((AppCompatActivity)getActivity())
//                                .getSupportFragmentManager();
//                        FrameLayout fl = getActivity().findViewById(R.id.flPersonalContainer);
//                        //fl.removeAllViews();
//                        FragmentTransaction ft = manager
//                                .beginTransaction();
//                        ft.add(R.id.flPersonalContainer, frag);
//                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                        ft.addToBackStack(null);
//                        ft.commit();
                    }
                };
            }

            @Override
            protected Component.Inflater makeInflater(MoodWrapper item) {
                return new ProgressGridMoodComponent.Inflater();
            }
        };

        goalsWellAdapter = new DataComponentAdapter<Goal>((DelegatedResultActivity) getActivity(), goodGoals) {
            @Override
            public Component makeComponent(Goal item, Component.ViewHolder holder) {
                ProgressGoalComponent component = new ProgressGoalComponent(item);
                component.setOnClickListener((goal) -> toggleShowOnly(goal));
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Goal item) {
                return new ProgressGoalComponent.Inflater();
            }
        };

        goalsWorkAdapter = new DataComponentAdapter<Goal>((DelegatedResultActivity) getActivity(), badGoals) {
            @Override
            public Component makeComponent(Goal item, Component.ViewHolder holder) {
                ProgressGoalComponent component = new ProgressGoalComponent(item);
                component.setOnClickListener((goal) -> toggleShowOnly(goal));
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Goal item) {
                return new ProgressGoalComponent.Inflater();
            }
        };

        rvMood.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvWell.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvWork.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvMood.setAdapter(gridMoodAdapter);
        rvWell.setAdapter(goalsWellAdapter);
        rvWork.setAdapter(goalsWorkAdapter);

        int margin = getResources().getDimensionPixelOffset(R.dimen.elementMargin);
        int outerMargin = getResources().getDimensionPixelOffset(R.dimen.outerMargin);
        EdgeDecorator thumbnailDecorator = new EdgeDecorator.Config(margin)
                .setFirstMargin(outerMargin)
                .setLastMargin(outerMargin)
                .setOrientation(EdgeDecorator.Orientation.Horizontal)
                .build();

        rvWell.addItemDecoration(thumbnailDecorator);
        rvWork.addItemDecoration(thumbnailDecorator);

        queryGoals(() -> {
            GoalUtils.sortGoals(mGoals, length, User.getCurrentUser(), (tsGoals) -> {
                Log.e("ProgressFragment", "Were in boys");
                //Toast.makeText(getContext(), "We entered", Toast.LENGTH_SHORT).show();
                for(int i = 0; i < mGoals.size(); i++){
                    if(tsGoals.size() == 0) {
                        break;
                    }
                    if(tsGoals.last().value >= 0 && goodGoals.size() < 3) {
                        goodGoals.add(tsGoals.last().item);
                        goalsWellAdapter.notifyItemInserted(i);
                        tsGoals.remove(tsGoals.last());
                        if(tsGoals.size() == 0){
                            break;
                        }
                    }
                    if(tsGoals.first().value < 0 && badGoals.size() < 3) {
                        badGoals.add(tsGoals.first().item);
                        goalsWorkAdapter.notifyItemInserted(i);
                        tsGoals.remove(tsGoals.first());
                    }
                }
                rvWell.scrollToPosition(0);
                rvWork.scrollToPosition(0);
                graphGoals.addAll(goodGoals);
                graphGoals.addAll(badGoals);

                if (goodGoals.size() == 0) {
                    tvWell.setVisibility(View.GONE);
                    rvWell.setVisibility(View.GONE);
                }

                if (badGoals.size() == 0) {
                    tvWork.setVisibility(View.GONE);
                    rvWork.setVisibility(View.GONE);
                }

                List<Integer>[] dataList = new List[graphGoals.size()];
                AsyncUtils.executeMany(graphGoals.size(), (i,cb) -> {
                    Goal goal = graphGoals.get(i);
                    getDataForGraph(goal, (data) -> {
                        dataList[i] = data;
                        cb.call(null);
                    });
                }, (err) -> {
                    //graphAdapter.notifyDataSetChanged();
                    for(int z = 0; z < graphGoals.size(); z++){
                        int color = getGoalGraphColor(graphGoals.get(z));
                        for(int j = 0; j < dataList[z].size(); j++){
                            graphManager.setValue(j, color, dataList[z].get(j));
                            graphManager.show(color);
                        }
                    }
                    if(graphManager.tallestY() == 0){
                        tvY2.setText("10");
                        tvY1.setText("5");
                    }else {
                        tvY2.setText(Integer.toString(graphManager.tallestY()));
                        tvY1.setText(Integer.toString(graphManager.tallestY() / 2));
                    }
                });
            });
        });

        hasDisplayedMood = false;
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!hasDisplayedMood) {
                hasDisplayedMood = true;

                int moodContainerWidth = rvMood.getWidth();
                int moodWidth = getResources().getDimensionPixelOffset(R.dimen.progressStackThickness);
                int moodMargin = (moodContainerWidth - (moodWidth * length)) / (length - 1);
                rvMood.addItemDecoration(new EdgeDecorator(new EdgeDecorator.Config(0, 0, moodMargin, 0)
                        .setLastMargin(0)
                        .setOrientation(EdgeDecorator.Orientation.Horizontal)));

                getDailyFirstPost((map, e) -> {
                    if (e != null) return;
                    MoodWrapper[] arr = new MoodWrapper[length];
                    AsyncUtils.executeMany(
                            length,
                            (i, cb) -> {
                                Date date = getDate(i);
                                Post post = map.get(date);
                                if (post == null) {
                                    arr[i] = new MoodWrapper(Mood.Status.Empty, date);
                                    cb.call(null);
                                    return;
                                }
                                post.fetchIfNeededInBackground((obj, e1) -> {
                                    if (e1 != null) {
                                        Log.e("ProgressFragment", "Failed to fetch post for mood", e);
                                        cb.call(e1);
                                        return;
                                    }
                                    arr[i] = new MoodWrapper(post.getMood(), post.getCreatedAt());
                                    cb.call(null);
                                });
                            },
                            (e1) -> {
                                for (int i = length - 1; i >= 0; i--) {
                                    userMoods.add(arr[i]);
                                }
                                gridMoodAdapter.notifyDataSetChanged();
                            }
                    );
                });
            }
        });
    }

    private int getGoalGraphColor(Goal goal){
        Integer color = hueMap.get(goal);
        if (color != null) return color;
        // Otherwise, choose new, unused color
        ColorUtils.Hue hue = goal.getHue();
        if(hue == null){
            if(hueBlue.size() == 0) hue = ColorUtils.Hue.Blue;
            if(hueOrange.size() == 0) hue = ColorUtils.Hue.Orange;
            if(huePurple.size() == 0) hue = ColorUtils.Hue.Purple;
            else hue = ColorUtils.Hue.random();
        }
        if(hue.equals(ColorUtils.Hue.Blue)) color = makeGraphColor(hue, hueBlue);
        if(hue.equals(ColorUtils.Hue.Orange)) color = makeGraphColor(hue, hueOrange);
        if(hue.equals(ColorUtils.Hue.Purple)) color = makeGraphColor(hue,huePurple);
        hueMap.put(goal, color);
        colorGoals.put(color, goal);
        return color;
    }

    private int makeGraphColor(ColorUtils.Hue hue, List<ColorUtils.Lightness> used) {
        ColorUtils.Lightness nextLightness = getNextLightness(used);
        used.add(nextLightness);
        int color;
        try {
            color = ColorUtils.getColor(getResources(), hue, nextLightness);
        } catch (IllegalStateException e) {
            color = new Random().nextInt();
        }
        if (verifyUniqueColor(color)) return color;
        return makeGraphColor(hue, used);
    }

    private ColorUtils.Lightness getNextLightness(List<ColorUtils.Lightness> used) {
        if (!used.contains(ColorUtils.Lightness.Light))      return ColorUtils.Lightness.Light;
        if (!used.contains(ColorUtils.Lightness.Mid))        return ColorUtils.Lightness.Mid;
        if (!used.contains(ColorUtils.Lightness.UltraLight)) return ColorUtils.Lightness.UltraLight;
        if (!used.contains(ColorUtils.Lightness.Dark))       return ColorUtils.Lightness.Dark;
        throw new IllegalArgumentException("Ran out of lightnesses.");
    }

    private boolean verifyUniqueColor(int color) {
        if(usedColors.size() == 0){
            return true;
        }
        for(int i = 0; i < usedColors.size(); i++){
            if(usedColors.get(i) == color){
                return false;
            }
        }
        return true;
    }

    public void getDataForGraph(Goal goal, AsyncUtils.ListCallback<Integer> callback){
        Integer[] dataPoints = new Integer[length];
        AsyncUtils.executeMany(
            length,
            (i, cb) -> {
                Date date = getDate(length - i);
                GoalUtils.getNumActionsComplete(date, goal, user, (num) -> {
                    dataPoints[i] = num;
                    cb.call(null);
                });
            },
            (err) -> {
                List<Integer> result = new ArrayList<>();
                Collections.addAll(result, dataPoints);
                callback.call(result);
            }
        );
    }

    private void getDailyFirstPost(AsyncUtils.TwoItemCallback<HashMap<Date, Post>, Throwable> callback) {
        HashMap<Date, Post> posts = new HashMap<>();
        journal.loadEntries(
            (query) -> {
                Date date = getDate(length);
                query.whereGreaterThanOrEqualTo(Post.KEY_CREATED_AT, date);
            },
            (e) -> {
                if (e != null) {
                    callback.call(posts, e);
                    return;
                }

                for (int i = 0; i < length; i++) {
                    Date date = getDate(i);
                    SortedList<Post> onDate = journal.getEntriesByDate(date);
                    int j = 0;
                    Post post = null;
                    Mood.Status lastMood = Mood.Status.Empty;
                    while (j < onDate.size() && lastMood == Mood.Status.Empty) {
                        post = onDate.get(j);
                        lastMood = post.getMood();
                        j++;
                    }
                    posts.put(date, post);
                }

                callback.call(posts, null);
            }
        );
    }

    private Date getDate(int daysAgo) {
        return TimeUtils.normalizeToDay(TimeUtils.getDaysAgo(new Date(), daysAgo));
    }

    public void queryGoals(AsyncUtils.EmptyCallback callback){
        User user = User.getCurrentUser();
        ParseQuery<Goal> goalQuery = new ParseQuery<Goal>(Goal.class);
        goalQuery.whereEqualTo("usersInvolved", user);
        //Log.d("ProgressFragment", "About to query");
        goalQuery.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                //Log.d("ProgressFragment", "querying");
                if(e != null){
                    Log.e("ProgressFragment", "Error with query");
                    e.printStackTrace();
                    return;
                }
                //Get all the goals
                mGoals.addAll(objects);
                //Log.d("ProgressFragment", "Finish with query, the size of mGoals is" + mGoals.size());
                callback.call();
            }
        });
    }

    private void configureGraphClick() {
        inSelectAnimation = false;

        graphManager.setOnClick((index, color, sectionView) -> {
            if (inSelectAnimation) return;
            Goal goal = colorGoals.get(color);
            if (goal == null) return;
            goal.fetchIfNeededInBackground((loaded, e) -> {
                if (e != null) {
                    Log.e("ProgressFragment", "Failed to fetch group on graph item click", e);
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    inSelectAnimation = true;

                    TextView tvSelected = new TextView(getActivity());
                    tvSelected.setText(Integer.toString(graphManager.valueOf(index, color)));
                    tvSelected.setTextColor(ColorUtils.getColor(getResources(), goal.getHue(), ColorUtils.Lightness.Dark));
                    tvSelected.setTypeface(tvSelected.getTypeface(), Typeface.BOLD);
                    tvSelected.setTextSize(18); // FIXME: Hardcoded because getResources() returned far too large a value
                    tvSelected.setAlpha(0f);
                    sectionView.addView(tvSelected);
                    tvSelected.setGravity(Gravity.CENTER);

                    graphManager.select(index, color);

                    tvSelected.animate()
                            .alpha(1f)
                            .setDuration(200) // TODO: Extract
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(800);
                                        } catch (InterruptedException e) {

                                        } finally {
                                            tvSelected.animate()
                                                    .alpha(0f)
                                                    .setDuration(200)
                                                    .setListener(new AnimatorListenerAdapter() {
                                                        @Override
                                                        public void onAnimationEnd(Animator animation) {
                                                            super.onAnimationEnd(animation);
                                                            sectionView.removeAllViews();
//                                                            sectionView.setLayoutParams(params);
                                                            graphManager.deselect();
                                                            inSelectAnimation = false;
                                                        }
                                                    });
                                        }
                                    }).run();
                                }
                            });
                });
            });
        });
    }

    private void toggleShowOnly(Goal goal) {
        int color = getGoalGraphColor(goal);
        if (graphManager.isOnlyShown(color)) {
            graphManager.hideAll();
            for (Integer toShow : lastShownColors) {
                graphManager.show(toShow);
            }
        } else {
            if (!graphManager.inShowOnlyMode()) lastShownColors = graphManager.getShown();
            graphManager.showOnly(color);
        }
    }

    private static class MoodWrapper {
        Mood.Status mood;
        Date date;

        MoodWrapper(Mood.Status mood, Date date) {
            this.mood = mood;
            this.date = date;
        }
    }

    private static class RemoveEndMarginDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == state.getItemCount() - 1) {
                outRect.right = 0;
            }
        }
    }
}
