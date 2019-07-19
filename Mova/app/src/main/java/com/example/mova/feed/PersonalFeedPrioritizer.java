package com.example.mova.feed;

import com.example.mova.model.Post;

import java.util.List;

public class PersonalFeedPrioritizer extends Prioritizer<Post> {

    public PersonalFeedPrioritizer() {
        super();
        addPriority((post) -> {
            return 0;
        });
    }

    @Override
    public float priorityOf(Post item) {
        return 0;
    }

    public List<Prioritized<Post>> makeSpecialCards() {
        return null;
    }
}
