package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class GoalUtils {

    public void getActionList(AsyncUtils.ListCallback<Action> callback, Goal goal, User user){
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
            for(Action action: actionList){
                if(TimeUtils.normalizeToDay(action.getCompletedAt()).equals(TimeUtils.normalizeToDay(date))){
                    numAction++;
                }
            }
            callback.call(numAction);
        }, goal, user);
    }

    public void getDataForGraph(Goal goal, User user, int length, AsyncUtils.ItemCallback<LineGraphSeries<DataPoint>> callback){
        DataPoint[] dataPoints = new DataPoint[length];
        AsyncUtils.executeMany(length, (i, cb) -> {
            Date date = new Date();
            //Make the day move a day earlier each iteration
            long dif = date.getTime() - 24*60*60*1000*i;
            date.setTime(dif);
            getNumActionsComplete(date, goal,user, (num) -> {
                dataPoints[i] = new DataPoint(i, num);
                cb.call(null);
            });

        }, () -> {callback.call(new LineGraphSeries<DataPoint>(dataPoints));} );

    }
}
