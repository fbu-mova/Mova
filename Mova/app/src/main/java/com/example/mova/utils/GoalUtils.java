package com.example.mova.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.mova.component.ComponentManager;
import com.example.mova.feed.Prioritized;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import static com.example.mova.model.Action.KEY_PARENT_USER;

public class GoalUtils {

    private static final String TAG = "GoalUtils";

    public interface onActionEditSaveListener {
        void call(String task, ComponentManager manager);
    }

    public static void getActionList(AsyncUtils.ListCallback<Action> callback, Goal goal, User user){
        ParseQuery<Action> pqAction = goal.relActions.getQuery();
        pqAction.whereEqualTo("parentUser", user);
        pqAction.findInBackground(new FindCallback<Action>() {
            @Override
            public void done(List<Action> objects, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public void getSharedActionList(AsyncUtils.ListCallback<SharedAction> callback, Goal goal){
        ParseQuery<SharedAction> pqSharedAction = goal.relSharedActions.getQuery();
        pqSharedAction.findInBackground(new FindCallback<SharedAction>() {
            @Override
            public void done(List<SharedAction> objects, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public void getNumActionsComplete(Date date, Goal goal, User user, AsyncUtils.ItemCallback<Integer> callback){
        getActionList((actionList) -> {
            int numAction = 0;
            for(Action action: actionList) {
                if (action.getIsDone()) {
                    if (TimeUtils.normalizeToDay(action.getCompletedAt()).equals(TimeUtils.normalizeToDay(date))) {
                        numAction++;
                    }
                }
            }
            callback.call(numAction);
        }, goal, user);
    }

    /**
     * Overloaded signature so that it doesn't calculate completion for one date only. Used to set progress in GoalProgressBar.
     * @param goal The parent goal.
     * @param user The user whose actions we want to track / progress we want to measure.
     * @param callback The callback to execute once the query is complete and we have the progress.
     */
    public static void getNumActionsComplete(Goal goal, User user, AsyncUtils.ItemCallback<Float> callback) {
        getActionList((actionList) -> {
            float totalAction = actionList.size();
            float doneAction = 0;
            for (Action action: actionList) {
                if (action.getIsDone()) {
                    doneAction++;
                }
            }
            callback.call(doneAction / totalAction);
        }, goal, user);
    }

    /**
     * Similar to above function, but has only one parameter of list of actions.
     * @param actions
     * @return
     */
    public static int numActionsComplete(List<Action> actions) {
        int completed = 0;
        for (Action action : actions) {
            completed += (action.getIsDone()) ? 1 : 0;
        }
        return completed;
    }

    public static int getProgressPercent(List<Action> actions) {
        int numComplete = numActionsComplete(actions);
        int percent = (int) Math.floor(100
                * (   ((double) numComplete)
                / ((double) actions.size())
        ));
        return percent;
    }

    public void getDataForGraph(Goal goal, User user, int length, AsyncUtils.ItemCallback<LineGraphSeries<DataPoint>> callback){
        DataPoint[] dataPoints = new DataPoint[length];
        AsyncUtils.executeMany(length, (i, cb) -> {
            Date date = new Date();
            //Make the day move a day earlier each iteration
            long dif = date.getTime() - 24*60*60*1000*(length - (i+1));
            date.setTime(dif);
            date = TimeUtils.normalizeToDay(date);
            Date finalDate = date;
            getNumActionsComplete(finalDate, goal,user, (num) -> {
                dataPoints[i] = new DataPoint(finalDate, num);
                cb.call(null);
            });

        }, (err) -> {callback.call(new LineGraphSeries<DataPoint>(dataPoints));} );

    }

    /**
     * Attaches a set of priority values to a list of goals based on the user's performance in each goal over a given period of time.
     * @param goalList The list of goals to prioritize.
     * @param length The amount of time elapsed (in days) in the
     * @param user The user whose goals should be prioritized.
     * @param callback
     */
    public void sortGoals(List<Goal> goalList, int length, User user, AsyncUtils.ItemCallback<TreeSet<Prioritized<Goal>>> callback){
        TreeSet<Prioritized<Goal>> tsGoals = new TreeSet<>();
        AsyncUtils.executeMany(goalList.size(), (i,cb) -> {
            Calendar cal = Calendar.getInstance();
            Date d1 = cal.getTime();
            cal.add(Calendar.DATE, -length + 1);
            Date d2 = cal.getTime();
            Goal goal = goalList.get(i);
            getNumActionsComplete(d1, goal, user, (num) -> {
                getNumActionsComplete(d2, goal, user, (num2) -> {
                    //Compare numbers and add value
                    Prioritized<Goal> pGoal = new Prioritized<Goal>(goal, num - num2);
                    tsGoals.add(pGoal);
                    Log.d("GoalUtils", "getNumActionsComplete inner callback");
                    cb.call(null);
                });
            });
        }, (e) -> {
            Log.d("GoalUtils", "final callback");
            callback.call(tsGoals);
        } );
    }

    public static void queryGoals(User user,AsyncUtils.ListCallback<Goal> callback){

        List<Goal> goals = new ArrayList<>();
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
                goals.addAll(objects);
                callback.call(goals);
            }
        });
    }

    public static void toggleDone(Action action, AsyncUtils.ItemCallback<Throwable> callback) {
        action.setIsDone(!action.getIsDone());
        action.saveInBackground((e) -> callback.call(e));
    }

    public static void submitGoal(String goalName, String goalDescription, List<Action> actions, boolean created, AsyncUtils.ItemCallback<Goal> finalCallback) {
        // todo -- include image choosing for goal image + color
        // todo -- update to also encompass Social functionality ? can share w/ group if not on personal feed

        Goal goal = new Goal()
                .setAuthor((User) ParseUser.getCurrentUser())
                .setTitle(goalName)
                .setDescription(goalDescription)
                .setIsPersonal(true); // fixme -- pass in as parameter to include Social functionality

        goal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Initial goal save successful");

                    saveSharedAction(actions, goal, created, finalCallback);
                }
                else {
                    Log.e(TAG, "Initial goal save unsuccessful", e);
                }
            }
        });
    }

    private static void saveSharedAction(List<Action> actions, Goal goal, boolean created, AsyncUtils.ItemCallback<Goal> finalCallback) {

        List<SharedAction> sharedActionsList = new ArrayList<>();

        // create a SharedAction for each action in actions

        AsyncUtils.waterfall(actions.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
            // the iteration in the for loop

            SharedAction sharedAction = new SharedAction()
                    .setTask(actions.get(item).getTask())
                    .setGoal(goal)
                    .setUsersDone(0)
                    .setIsPriority(actions.get(item).getIsPriority());

            sharedActionsList.add(sharedAction);

            // save sharedAction
            sharedAction.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Saved SharedAction successfully");
                    } else {
                        Log.e(TAG, "SharedAction failed saving", e);
                    }
                    callback.call(e);
                }
            });
        }, (e) -> {
            // when whole for-loop has run though -- save goal
            Log.d(TAG, "in saveSharedAction final callback");

            // go to create and save actions
            saveAction(actions, goal, sharedActionsList, created, finalCallback);
        });

    }

    private static void saveAction(List<Action> actions, Goal goal, List<SharedAction> sharedActionsList, boolean created, AsyncUtils.ItemCallback<Goal> finalCallback) {

        AsyncUtils.waterfall(actions.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
            // the iteration in the for loop

//            // save the Action
//            Action action = new Action()
//                    .setTask(actions.get(item))
//                    .setParentGoal(goal)
//                    .setParentUser((User) ParseUser.getCurrentUser())
//                    .setParentSharedAction(sharedActionsList.get(item))
//                    .setIsConnectedToParent(true)
//                    .setIsDone(false);

            // save action -- fixme: still needs to be saved again?
            actions.get(item).saveInBackground((ParseException e) -> {
                if (e == null) {
                    Log.d(TAG, "Saved Action successfully");

                    // add to specific sharedAction's relation
                    sharedActionsList.get(item).relChildActions.add(actions.get(item));
                    sharedActionsList.get(item).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "sharedAction added child action rel");
                            }
                            else {
                                Log.e(TAG, "sharedAction failed to add child action rel", e);
                            }
                            callback.call(e);
                        }
                    });
                }
                else {
                    Log.e(TAG, "Action failed saving", e);
                    callback.call(e);
                }
            });
        }, (e) -> {
            // when whole for-loop has run though -- save goal
            Log.d(TAG, "in saveAction final callback");

            updateGoalRels(sharedActionsList, actions, goal, created, finalCallback);
        });
    }

    private static void updateGoalRels(List<SharedAction> sharedActionsList, List<Action> actionsList, Goal goal, boolean created, AsyncUtils.ItemCallback<Goal> finalCallback) {

        AsyncUtils.executeMany(sharedActionsList.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
            // the iteration in the for loop

            if (created) goal.relSharedActions.add(sharedActionsList.get(item));
            goal.relActions.add(actionsList.get(item)); // has same length as sharedActionsList
            callback.call(null);
        }, (e) -> {

            goal.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "finished final goal update");

                        // finally add whole goal to user's goal relation
                        saveGoalToUser(goal, finalCallback);
                    }
                    else {
                        Log.e(TAG, "failed to finish final goal update", e);
                    }
                }
            });
        });
    }

    private static void saveGoalToUser(Goal goal, AsyncUtils.ItemCallback<Goal> finalCallback) {

        (User.getCurrentUser()).relGoals.add(goal, finalCallback);
    }


    public static void saveSocialGoal(Goal goal) {
        /* TO save a social goal as a personal goal:
        1. add the social goal to the relation of the User's goals
        2. for each shared action in the goal, create a new action pointing to that shared action
        3. make the new action point to the user
        4. add User to the usersInvolved relation of the goal
        5. add all actions to the actions relation of a goal (fixme - w/ many actions, easier to save to user instead of goal)

        being edited: goal's usersInvolved, actions
                        each sharedAction's actions
                        User's goals
         */

        // first create each action per sharedAction:
        // 1) get List<SharedAction> and List<String> actions (to use saveAction in GoalCompose)
        //        calling saveAction does 2. 3. 5. 1. in that order
        // do step 4

        // not extracted nicely, but does the job i guess

        // extract List<SharedAction> sharedActionsList and List<String> actionsList

        goal.relSharedActions.getList((sharedActions) -> {

            // TODO -- need to create the actions from relSharedAction then save via saveAction
            // another waterfall :(
            List<Action> actionsList = new ArrayList<>();
            AsyncUtils.waterfall(sharedActions.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
                // in for loop
                SharedAction sharedAction = sharedActions.get(item);

                Action action = new Action()
                        .setIsDone(false)
                        .setIsConnectedToParent(true)
                        .setParentSharedAction(sharedAction)
                        .setParentUser(User.getCurrentUser())
                        .setParentGoal(sharedAction.getGoal())
                        .setTask(sharedAction.getTask())
                        .setIsPriority(sharedAction.getIsPriority());

                action.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            actionsList.add(action);
                        }
                        else {
                            Log.e(TAG, "error saving action in saveSocialGoal", e);
                        }
                        callback.call(e);
                    }
                });

            }, (e) -> {
                // after loop complete
                saveAction(actionsList, goal, sharedActions, false, (item) -> {
                    // add User to usersInvolved relation of goal + save goal
                    goal.relUsersInvolved.add(User.getCurrentUser(), (user) -> {});
                });
            });

        });

    }

    /**
     * Saves the action as well as updates the parent sharedAction.
     * @param action Action
     * @param new_action String
     * @param callback
     */
    public static void saveSharedAndAction(Action action, String new_action, AsyncUtils.ItemCallback<Action> callback) {
        action.setTask(new_action);

        action.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "action saved successfully");

                    SharedAction sharedAction = action.getParentSharedAction();
                    sharedAction.setTask(new_action).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "shared action saved");
                                callback.call(action);
                            }
                            else {
                                Log.e(TAG, "shared action failed saving", e);
                            }
                        }
                    });
                }
                else {
                    Log.e(TAG, "action failed saving", e);
                }
            }
        });
    }

    public static void loadGoalActions(Goal goal, AsyncUtils.ListCallback<Action> callback) {
        // make query calls to get the user's actions for a goal
        ParseQuery<Action> actionQuery = goal.relActions.getQuery();
        actionQuery.whereEqualTo(KEY_PARENT_USER, User.getCurrentUser())
            .orderByDescending(Action.KEY_CREATED_AT);

        actionQuery.findInBackground(new FindCallback<Action>() {
            @Override
            public void done(List<Action> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "action query success w/ size " + objects.size());
                    callback.call(objects);
                }
                else {
                    Log.e(TAG, "query for actions failed", e);
                }
            }
        });
    }

    public static void loadGoalSharedActions(Goal goal, AsyncUtils.ListCallback<SharedAction> callback) {

        goal.relSharedActions.getQuery()
                .orderByDescending(SharedAction.KEY_CREATED_AT)
                .findInBackground((objects, e) -> {
                    if (e == null) {
                        Log.d(TAG, "sharedAction query success w/ size " + objects.size());
                        callback.call(objects);
                    }
                    else {
                        Log.e(TAG, "query for sharedActions failed", e);
                    }
                });
    }

    public static void checkIfUserInvolved(Goal goal, User user, AsyncUtils.ItemCallback<Boolean> callback) {
        ParseQuery<Goal> goalQuery = user.relGoals.getQuery().whereEqualTo("objectId", goal.getObjectId());
        goalQuery.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                boolean check = false;
                if (e == null) {
                    if (objects.size() == 1 && objects.get(0).equals(goal)) {
                        check = true;
                    }
                }
                else {
                    Log.e(TAG, "error querying for goal");
                }
                callback.call(check);
            }
        });
    }

}
