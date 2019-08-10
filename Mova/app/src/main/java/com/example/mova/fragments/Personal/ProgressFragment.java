package com.example.mova.fragments.Personal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.ProgressStack;
import com.example.mova.ProgressStackManager;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.adapters.ViewAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.ProgressGoalComponent;
import com.example.mova.components.ProgressGridMoodComponent;
import com.example.mova.model.Goal;
import com.example.mova.model.Journal;
import com.example.mova.model.Mood;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    //Progress Stacks
    private List<ProgressStack> progressStacks;
    @BindView(R.id.progressStack1) protected ProgressStack ps1;
    @BindView(R.id.progressStack2) protected ProgressStack ps2;
    @BindView(R.id.progressStack3) protected ProgressStack ps3;
    @BindView(R.id.progressStack4) protected ProgressStack ps4;
    @BindView(R.id.progressStack5) protected ProgressStack ps5;
    @BindView(R.id.progressStack6) protected ProgressStack ps6;
    @BindView(R.id.progressStack7) protected ProgressStack ps7;

    private HashMap<Goal, Integer> hueMap;
    private HashMap<Integer, Goal> colorGoals;
    private List<Integer> usedColors;
    private List<Goal> mGoals;
    private List<Goal> graphGoals;
    private List<Goal> goodGoals;
    private List<Goal> badGoals;
    private List<Mood.Status> userMoods;
    private ProgressStackManager graphManager;

    private int length = 0;
    private User user;
    private ViewAdapter<ProgressStack> graphAdapter;
    private DataComponentAdapter<Mood.Status> gridMoodAdapter;
    private DataComponentAdapter<Goal> goalsWellAdapter;
    private DataComponentAdapter<Goal> goalsWorkAdapter;

    private boolean inSelectAnimation;

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

        hueMap = new HashMap<>();
        usedColors = new ArrayList<>();
        mGoals = new ArrayList<>();
        graphGoals = new ArrayList<>();
        colorGoals = new HashMap<>();
        userMoods = new ArrayList<>();
        goodGoals = new ArrayList<>();
        badGoals = new ArrayList<>();
        graphManager = new ProgressStackManager(getActivity(), progressStacks);

        journal = new Journal(User.getCurrentUser());

        configureGraphClick();

        // Create adapters

        gridMoodAdapter = new DataComponentAdapter<Mood.Status>((DelegatedResultActivity) getActivity(), userMoods) {
            @Override
            public Component makeComponent(Mood.Status item, Component.ViewHolder holder) {
                Component component = new ProgressGridMoodComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Mood.Status item) {
                return new ProgressGridMoodComponent.Inflater();
            }
        };

        goalsWellAdapter = new DataComponentAdapter<Goal>((DelegatedResultActivity) getActivity(), goodGoals) {
            @Override
            public Component makeComponent(Goal item, Component.ViewHolder holder) {
                Component component = new ProgressGoalComponent(item);
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
                Component component = new ProgressGoalComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Goal item) {
                return new ProgressGoalComponent.Inflater();
            }
        };

        rvMood.setLayoutManager(new GridLayoutManager(getActivity(), length));
        rvWell.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvWork.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvMood.setAdapter(gridMoodAdapter);
        rvWell.setAdapter(goalsWellAdapter);
        rvWork.setAdapter(goalsWorkAdapter);

        // TODO: Add edge decorators

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

                AsyncUtils.executeMany(graphGoals.size(), (i,cb) -> {
                    Goal goal = graphGoals.get(i);
                    int color = getGoalGraphColor(goal);
                    getDataForGraph(goal, user, length, (data) -> {
                        for (int j = 0; j < data.size(); j++){
                            graphManager.show(color);
                            graphManager.setValue(j, color, data.get(j));
                        }
                        cb.call(null);
                    });
                }, (err) -> {
                    //graphAdapter.notifyDataSetChanged();
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
    }

    private int getGoalGraphColor(Goal goal){
        Integer color = hueMap.get(goal);
        if (color != null) return color;
        // Otherwise, choose new, unused color
        ColorUtils.Hue hue = goal.getHue();
        if (hue == null) hue = ColorUtils.Hue.random();
        color = makeGraphColor(hue, new ArrayList<>());
        hueMap.put(goal, color);
        colorGoals.put(color, goal);
        return color;
    }

    private int makeGraphColor(ColorUtils.Hue hue, List<ColorUtils.Lightness> used) {
        ColorUtils.Lightness nextLightness = getNextLightness(used);
        used.add(nextLightness);
        int color = ColorUtils.getColor(getResources(), hue, nextLightness);
        if (verifyUniqueColor(color)) return color;
        return makeGraphColor(hue, used);
    }

    private ColorUtils.Lightness getNextLightness(List<ColorUtils.Lightness> used) {
        if (!used.contains(ColorUtils.Lightness.Light))      return ColorUtils.Lightness.Light;
        if (!used.contains(ColorUtils.Lightness.Mid))        return ColorUtils.Lightness.Mid;
        if (!used.contains(ColorUtils.Lightness.Dark))       return ColorUtils.Lightness.Dark;
        if (!used.contains(ColorUtils.Lightness.UltraLight)) return ColorUtils.Lightness.UltraLight;
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

    public static void getDataForGraph(Goal goal, User user, int length, AsyncUtils.ListCallback<Integer> callback){
        List<Integer> dataPoints = new ArrayList<>();
        AsyncUtils.waterfall(
            length,
            (i, cb) -> {
                Date date = new Date();
                //Make the day move a day earlier each iteration
                long dif = date.getTime() - 24*60*60*1000*(length - (i+1));
                date.setTime(dif);
                date = TimeUtils.normalizeToDay(date);
                Date finalDate = date;
                GoalUtils.getNumActionsComplete(finalDate, goal, user, (num) -> {
                    dataPoints.add(num);
                    cb.call(null);
                });
            },
            (err) -> {callback.call(dataPoints);}
        );

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
}
