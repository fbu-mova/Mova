package com.example.mova.fragments.Personal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
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
import androidx.recyclerview.widget.SortedList;

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
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.TimeUtils;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
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

    @BindView(R.id.rvGraph) protected RecyclerView rvGraph;
    @BindView(R.id.tvY1)    protected TextView tvY1;
    @BindView(R.id.tvY2)    protected TextView tvY2;
    @BindView(R.id.rvMood)  protected RecyclerView rvMood;

    @BindView(R.id.rvWell)  protected RecyclerView rvWell;
    @BindView(R.id.rvWork)  protected RecyclerView rvWork;

    private List<Goal> mGoals;
    private HashMap<Integer, Goal> colorGoals;
    private List<Goal> goodGoals;
    private List<Goal> badGoals;
    private List<Mood.Status> userMoods;
    //protected TreeSet<Prioritized<Goal>> prioGoals;
    private ProgressStackManager graphManager;

    private int length = 0;
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

        length = 7;

        mGoals = new ArrayList<>();
        colorGoals = new HashMap<>();
        userMoods = new ArrayList<>();
        goodGoals = new ArrayList<>();
        badGoals = new ArrayList<>();
        //prioGoals = new TreeSet<>();
        graphManager = new ProgressStackManager(getActivity(), length);

        journal = new Journal(User.getCurrentUser());

        configureGraphClick();

        //create the adapter

        graphAdapter = new ViewAdapter<ProgressStack>(getActivity(), graphManager.getStacks()) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder<ProgressStack> holder, int position) {

            }
        };

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

        rvGraph.setLayoutManager(new GridLayoutManager(getActivity(), length));
        rvMood.setLayoutManager(new GridLayoutManager(getActivity(), length));
        rvWell.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvWork.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvMood.setAdapter(gridMoodAdapter);
        rvWell.setAdapter(goalsWellAdapter);
        rvWork.setAdapter(goalsWorkAdapter);

        // TODO: Add edge decorators

        // TODO: Adapt logic to new graph

//        queryGoals(() -> setGraph(() -> {
//            Calendar cal = Calendar.getInstance();
//            Date d1 = cal.getTime();
//            cal.add(Calendar.DATE, -length + 1);
//            Date d2 = cal.getTime();
//            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(),new SimpleDateFormat("MM/dd")));
//            graph.getViewport().setXAxisBoundsManual(true);
//            graph.getViewport().setMinX(Long.valueOf(d2.getTime()).doubleValue());
//            graph.getViewport().setMaxX(Long.valueOf(d1.getTime()).doubleValue());
//            graph.getGridLabelRenderer().setHumanRounding(false);
//            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
//            graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
//            //graph.getGridLabelRenderer().setVerticalAxisTitle("Actions Completed");
//
//
//            //Toast.makeText(getContext(), "Graph created", Toast.LENGTH_SHORT).show();
//            //Log.e("ProgressFragment", "Were not in boys");
//
//            //organize goals and create adapter
//            goalUtils.sortGoals(mGoals, length, User.getCurrentUser(), (tsGoals) -> {
//                Log.e("ProgressFragment", "Were in boys");
//                //Toast.makeText(getContext(), "We entered", Toast.LENGTH_SHORT).show();
//                for(int i = 0; i < mGoals.size(); i++){
//                    if(tsGoals.size() == 0) {
//                        break;
//                    }
//                    if(tsGoals.first().value >= 0 && goodGoals.size() < 3) {
//                        goodGoals.add(tsGoals.first().item);
//                        goalsWellAdapter.notifyItemInserted(i);
//                        tsGoals.remove(tsGoals.first());
//                        if(tsGoals.size() == 0){
//                            break;
//                        }
//                    }
//                    if(tsGoals.last().value < 0 && badGoals.size() < 3) {
//                        badGoals.add(tsGoals.last().item);
//                        goalsWorkAdapter.notifyItemInserted(i);
//                        tsGoals.remove(tsGoals.last());
//                    }
//
//                }
//                rvWell.scrollToPosition(0);
//                rvWork.scrollToPosition(0);
//
//            });
//
//
//
//        }));

//        journal.loadEntries((e) -> {
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.DATE, -length + 1);
//            Date d1 = cal.getTime();
//            for(int i = 0; i < length; i++){
//                SortedList<Post> posts = journal.getEntriesByDate(TimeUtils.normalizeToDay(d1));
//                if(posts.size() == 0){
//                    userMoods.add(Mood.Status.Empty);
//                }else {
//                    userMoods.add(posts.get(0).getMood());
//                }
//                cal.add(Calendar.DATE, 1);
//                d1 = cal.getTime();
//            }
//            gridMoodAdapter.notifyDataSetChanged();
//            gvMood.scrollTo(0,0);
//        });
//        queryPosts(() -> {
//            getListOnePostPerDay();
//            gridMoodAdapter.notifyDataSetChanged();
//            gvMood.scrollTo(0,0);
//        });
    }

//    private void setGraph(AsyncUtils.EmptyCallback callback){
//        AsyncUtils.executeMany(mGoals.size(), (i,cb) -> {
//           Goal goal = mGoals.get(i);
//            goalUtils.getDataForGraph(goal, User.getCurrentUser(), length , (series) -> {
//                series.setTitle(goal.getTitle());
//                series.setSpacing(10);
//
//                ColorUtils.Hue hue = goal.getHue();
//                if (hue == null) hue = ColorUtils.Hue.random();
//                int mid = ColorUtils.getColor(getResources(), hue, ColorUtils.Lightness.Mid);
//
//                series.setColor(mid);
//                graph.addSeries(series);
//                cb.call(null);
////                        graph.getViewport().setMinX(-1*length);
//            });
//
//        }, (e) -> {callback.call();});
//
//    }


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

//    public void queryPosts(AsyncUtils.EmptyCallback callback){
//        User user = (User) ParseUser.getCurrentUser();
//        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
//        postQuery.whereEqualTo("author", user);
//        postQuery.whereEqualTo("isPersonal", true);
//        postQuery.orderByDescending("createdAt");
//        postQuery.findInBackground(new FindCallback<Post>() {
//            @Override
//            public void done(List<Post> objects, ParseException e) {
//                if(e != null){
//                    Log.e("ProgressFragment", "Error with queryPosts");
//                    e.printStackTrace();
//                    return;
//                }
//                userPosts.addAll(objects);
//                callback.call();
//            }
//        });
//    }
//
//    public void getListOnePostPerDay(){
//        Date date = new Date();
//        for(int i  = 0; i < userPosts.size(); i++){
//            //Intialize date as the date of the first entry
//            if(i == 0){
//                date = TimeUtils.normalizeToDay(userPosts.get(i).getCreatedAt());
//            }
//            //If the date of the next element is equal to the previouis one, delete it
//            else if(date == TimeUtils.normalizeToDay(userPosts.get(i).getCreatedAt())){
//                userPosts.remove(i);
//            }
//            //If the date is less, make this the new date
//            else{
//                date = TimeUtils.normalizeToDay(userPosts.get(i).getCreatedAt());
//            }
//        }
//    }

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

                inSelectAnimation = true;

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sectionView.getLayoutParams();
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(params.width, params.height, Gravity.CENTER);
                sectionView.setLayoutParams(newParams);

                TextView tvSelected = new TextView(getActivity());
                tvSelected.setText(graphManager.valueOf(index, color));
                tvSelected.setTextColor(ColorUtils.getColor(getResources(), goal.getHue(), ColorUtils.Lightness.Dark));
                tvSelected.setTextSize(getResources().getDimensionPixelSize(R.dimen.textSizeFocus));
                tvSelected.setAlpha(0f);
                sectionView.addView(tvSelected);

                graphManager.select(index, color);

                tvSelected.animate()
                    .alpha(1f)
                    .setDuration(300) // TODO: Extract
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {

                                } finally {
                                    tvSelected.animate()
                                        .alpha(0f)
                                        .setDuration(300)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                sectionView.removeAllViews();
                                                sectionView.setLayoutParams(params);
                                                inSelectAnimation = false;
                                            }
                                        });
                                }
                            }).run();
                        }
                    });
            });
        });
    }
}
