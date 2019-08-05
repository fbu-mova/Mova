package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("SharedAction")
public class SharedAction extends HashableParseObject {

    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_TASK = "task";
    public static final String KEY_GOAL = "goal";
    public static final String KEY_USERS_DONE = "usersDone";

    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_MANUAL_CREATED_AT = "manualCreatedAt";

    public static final String KEY_IS_PRIORITY = "isPriority";
    public static final String KEY_RECURRENCE = "recurrence";
    public static final String KEY_RECURRENCE_ID = "recurrenceId";

    //Relations
    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_CHILD_ACTIONS = "childActions";
    public final RelationFrame<User> relUsersInvolved = new RelationFrame<>(this, KEY_USERS_INVOLVED);
    public final RelationFrame<Action> relChildActions = new RelationFrame<>(this, KEY_CHILD_ACTIONS);

    //Task
    public String getTask(){
        return getString(KEY_TASK);
    }

    public SharedAction setTask(String task){
        put(KEY_TASK, task);
        return this;
    }

    //Goal
    public Goal getGoal(){
        return (Goal) getParseObject(KEY_GOAL);
    }

    public SharedAction setGoal(Goal goal){
        put(KEY_GOAL, goal);
        return this;
    }


    //Users done

    public int getUsersDone(){
        return getInt(KEY_USERS_DONE);
    }

    public SharedAction setUsersDone(int done){
        put(KEY_USERS_DONE, done);
        return this;
    }

    public static class Data {
        public SharedAction sharedAction;
        public boolean isUserDone;

        public Data(SharedAction sharedAction, boolean isUserDone) {
            this.sharedAction = sharedAction;
            this.isUserDone = isUserDone;
        }
    }

    // Created At (manual data since can't set created at directly on ParseObject);
    @Override
    public Date getCreatedAt() {
        Date manual = getDate(KEY_MANUAL_CREATED_AT);
        Date result = (manual != null) ? manual : super.getCreatedAt();
        return result;
    }

    public SharedAction setCreatedAt(Date date) {
        put(KEY_MANUAL_CREATED_AT, date);
        return this;
    }

    // Recurrence
    public List<Recurrence> getRecurrence() {
        String recurrenceStr = getString(KEY_RECURRENCE);
        return Recurrence.parse(recurrenceStr);
    }

    public SharedAction setRecurrence(Recurrence recurrence) {
        put(KEY_RECURRENCE, recurrence.toString());
        if (getRecurrenceId() == null) {
            setRecurrenceId(getObjectId());
        }
        return this;
    }

    public SharedAction setRecurrence(List<Recurrence> recurrence) {
        put(KEY_RECURRENCE, Recurrence.toString(recurrence));
        if (getRecurrenceId() == null) {
            setRecurrenceId(getObjectId());
        }
        return this;
    }

    public SharedAction addRecurrence(Recurrence recurrence) {
        String existingRecurrence = getString(KEY_RECURRENCE);
        String compounded = Recurrence.add(existingRecurrence, recurrence);
        put(KEY_RECURRENCE, compounded);
        if (getRecurrenceId() == null) {
            setRecurrenceId(getObjectId());
        }
        return this;
    }

    public SharedAction removeRecurrence()  {
        put(KEY_RECURRENCE, JSONObject.NULL);
        put(KEY_RECURRENCE_ID, JSONObject.NULL);
        return this;
    }

    public boolean isRecurring() {
        List<Recurrence> recurrence = getRecurrence();
        return recurrence.size() > 0;
    }

    /**
     * Determines whether or not the action's next recurrence(s) should be created and added to the DB.
     * @return The recurrences for which the action should be duplicated.
     */
    public List<Recurrence> shouldAddRecur() {
        List<Recurrence> recurrence = getRecurrence();
        Date now = new Date();

        List<Recurrence> result = new ArrayList<>();

        // Check for no recurrence or empty recurrence
        if (recurrence.size() == 0
                || recurrence.contains(Recurrence.makeEmpty())) return result;

        for (Recurrence r : recurrence) {
            // If the next relative date has already happened, then true
            Date nextDate = r.nextRelativeDate(getCreatedAt());
            if (now.compareTo(nextDate) >= 0) {
                result.add(r);
            }
        }

        return result;
    }

    /**
     * Creates and adds the next recurring action if the action is recurring.
     * Adds empty to the recurrence of the current action to deprecate it.
     * @return The new action. Null if no action was created.
     */
    public void addRecur(AsyncUtils.TwoItemCallback<List<SharedAction>, Throwable> callback) {
        List<Recurrence> toAdd = shouldAddRecur();
        if (toAdd.size() == 0) callback.call(new ArrayList<>(), null);

        List<SharedAction> sharedActions = new ArrayList<>();

        AsyncUtils.executeMany(
            toAdd.size(),
            (i, cb) -> {
                Recurrence r = toAdd.get(i);
                Date manual = r.nextRelativeDate(getCreatedAt());
                SharedAction sharedAction = makeDatedRecurAction(manual);
                sharedAction.getGoal().fetchIfNeededInBackground((object, e) -> {
                    if (e != null) {
                        cb.call(e);
                        return;
                    }
                    Goal goal = (Goal) object;
                    goal.relSharedActions.add(sharedAction, (sameSA) -> {
                        sharedAction.saveInBackground((e1) -> {
                            if (e1 == null) {
                                sharedActions.add(sharedAction);
                            }
                            cb.call(e1);
                        });
                    });
                });
            },
            (e) -> {
                callback.call(sharedActions, e);
            }
        );
    }

    private SharedAction makeDatedRecurAction(Date manual) {
        SharedAction sharedAction = new SharedAction()
                .setCreatedAt(manual)
                .setTask(getTask())
                .setRecurrenceId(getRecurrenceId())
                .setRecurrence(getRecurrence())
                .setGoal(getGoal())
                .setUsersDone(0);

        return sharedAction;
    }

    // Recurrence ID
    public String getRecurrenceId() {
        return getString(KEY_RECURRENCE_ID);
    }

    public SharedAction setRecurrenceId(String id) {
        put(KEY_RECURRENCE_ID, id);
        return this;
    }

    /**
     * Fetches the most recent copy of a recurring action.
     * Provides null if the action is not recurring or no action could be found.
     */
    public void getMostRecentRecur(AsyncUtils.TwoItemCallback<SharedAction, Throwable> cb) {
        if (!isRecurring()) cb.call(null, null);

        ParseQuery<SharedAction> query = makeRecentRecurQuery();
        query.setLimit(1);
        query.findInBackground((sharedActions, e) -> {
            SharedAction item = (sharedActions != null && sharedActions.size() > 0) ? sharedActions.get(0) : null;
            cb.call(item, e);
        });
    }

    public void getAllMoreRecentRecurs(AsyncUtils.TwoItemCallback<List<SharedAction>, Throwable> cb) {
        if (!isRecurring()) cb.call(new ArrayList<>(), null);

        ParseQuery<SharedAction> query = makeRecentRecurQuery();
        Date manualCreatedAt = getDate(KEY_MANUAL_CREATED_AT);
        // FIXME: Again, will this query work?
        query.whereGreaterThan(KEY_MANUAL_CREATED_AT, (manualCreatedAt == null) ? JSONObject.NULL : manualCreatedAt);
        query.whereGreaterThan(KEY_CREATED_AT, getCreatedAt());
        query.orderByAscending(KEY_MANUAL_CREATED_AT);
        query.addAscendingOrder(KEY_CREATED_AT);

        query.findInBackground((sharedActions, e) -> cb.call(sharedActions, e));
    }

    private ParseQuery<SharedAction> makeRecentRecurQuery() {
        String id = getRecurrenceId();
        ParseQuery<SharedAction> query = new ParseQuery<>(SharedAction.class);

        query.whereEqualTo(KEY_RECURRENCE_ID, id);
        // FIXME: Will this query work?
        query.orderByDescending(KEY_MANUAL_CREATED_AT);
        query.addDescendingOrder(KEY_CREATED_AT);

        return query;
    }
}
