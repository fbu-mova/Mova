package com.example.mova.model;

import android.graphics.Bitmap;
import android.media.Image;

import com.example.mova.components.Component;
import com.example.mova.components.ImageComponent;
import com.example.mova.components.MediaTextComponent;
import com.example.mova.components.PostComponent;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ImageUtils;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;

@ParseClassName("Media")
public class Media extends HashableParseObject {

    public static final String KEY_TYPE = "type";
    public static final String KEY_PARENT = "parent";
    public static final String KEY_POST = "contentPost";
    public static final String KEY_GROUP = "contentGroup";
    public static final String KEY_EVENT = "contentEvent";
    public static final String KEY_GOAL = "contentGoal";
    public static final String KEY_ACTION = "contentAction";
    public static final String KEY_TEXT = "contentText";
    public static final String KEY_IMAGE = "contentImage";

    public Media() { }

    public Media(Object content) {
        setContent(content);
    }

    // Type
    public ContentType getType() {
        int type = getInt(KEY_TYPE);
        return ContentType.fromValue(type);
    }

    public Media setType(ContentType type) {
        put(KEY_TYPE, type.getValue());
        return this;
    }

    //Parent
    public Post getParent(){
        return (Post) getParseObject(KEY_PARENT);
    }

    public Media setParent(Post post){
        put(KEY_PARENT, post);
        return this;
    }

    // ContentText
    public String getContentText() {
        return getString(KEY_TEXT);
    }

    public Media setContentText(String text) {
        put(KEY_TEXT, text);
        return this;
    }

    // ContentPost
    public Post getContentPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public Media setContentPost(Post post){
        put(KEY_POST, post);
        return this;
    }

    // ContentGroup
    public Group getContentGroup() {
        return (Group) getParseObject(KEY_GROUP);

    }

    public Media setContentGroup(Group group) {
        put(KEY_GROUP,group);
        return this;
    }

    // ContentEvent
    public Event getContentEvent() {
        return (Event) getParseObject(KEY_EVENT);
    }

    public Media setContentEvent(Event event) {
        put(KEY_EVENT, event);
        return this;
    }

    // ContentGoal
    public Goal getContentGoal() {
        return (Goal) getParseObject(KEY_GOAL);
    }

    public Media setContentGoal(Goal goal) {
        put(KEY_GOAL, goal);
        return this;
    }

    // ContentAction
    public Action getContentAction() {
        return (Action) getParseObject(KEY_ACTION);
    }

    public Media setContentAction(Action action) {
        put(KEY_ACTION, action);
        return this;
    }

    // ContentImage
    public ParseFile getContentImage() {
        return getParseFile(KEY_IMAGE);
    }

    public Media setContentImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
        return this;
    }

    // Handle content item regardless of type
    public Object getContent() {
        switch (getType()) {
            case Post:
                return getContentPost();
            case Group:
                return getContentGroup();
            case Event:
                return getContentEvent();
            case Goal:
                return getContentGoal();
            case Action:
                return getContentAction();
            case Image:
                return getContentImage();
            case Text:
            default:
                return getContentText();
        }
    }

    public Media setContent(Object content) {
        if (content instanceof Post) {
            setType(ContentType.Post);
            setContentPost((Post) content);
        } else if (content instanceof Group) {
            setType(ContentType.Group);
            setContentGroup((Group) content);
        } else if (content instanceof Event) {
            setType(ContentType.Event);
            setContentEvent((Event) content);
        } else if (content instanceof Goal) {
            setType(ContentType.Goal);
            setContentGoal((Goal) content);
        } else if (content instanceof Action) {
            setType(ContentType.Action);
            setContentAction((Action) content);
        } else if (content instanceof ParseFile) {
            setType(ContentType.Image);
            setContentImage((ParseFile) content);
        } else {
            setType(ContentType.Text);
            setContentText(content.toString());
        }
        return this;
    }

    public Class getObjectType() {
        switch (getType()) {
            case Post:
                return Post.class;
            case Group:
                return Group.class;
            case Event:
                return Event.class;
            case Goal:
                return Goal.class;
            case Action:
                return Action.class;
            case Image:
                return ParseFile.class;
            case Text:
            default:
                return String.class;
        }
    }

    public Component makeComponent() {
        switch (getType()) {
            case Text:
                return new MediaTextComponent(this);
            case Image:
                return new ImageComponent(getContentImage());
            case Post:
                return new PostComponent(getContentPost());
            default:
                return null;
        }
    }

    public static enum ContentType {
        Text(0),
        Post(1),
        Group(2),
        Event(3),
        Goal(4),
        Action(5),
        Image(6);

        private final int value;
        private ContentType(int value) {
            this.value = value;
        }

        public static ContentType fromValue(int value) {
            switch (value) {
                case 1:
                    return Post;
                case 2:
                    return Group;
                case 3:
                    return Event;
                case 4:
                    return Goal;
                case 5:
                    return Action;
                case 6:
                    return Image;
                case 0:
                default:
                    return Text;
            }
        }

        public String toKey() {
            switch (this) {
                case Post:
                    return KEY_POST;
                case Group:
                    return KEY_GROUP;
                case Event:
                    return KEY_EVENT;
                case Goal:
                    return KEY_GOAL;
                case Action:
                    return KEY_ACTION;
                case Image:
                    return KEY_IMAGE;
                case Text:
                default:
                    return KEY_TEXT;
            }
        }

        public int getValue() {
            return value;
        }
    }

    public static void fromImage(Bitmap bmp, AsyncUtils.TwoItemCallback<Media, Throwable> cb) {
        // TODO: Likely move image storage to some other cloud platform
        Media media = new Media();
        ParseFile parseFile = ImageUtils.bitmapToParse(bmp);
        parseFile.saveInBackground((SaveCallback) (e) -> {
            media.setContent(parseFile);
            cb.call(media, e);
        });
    }
}
