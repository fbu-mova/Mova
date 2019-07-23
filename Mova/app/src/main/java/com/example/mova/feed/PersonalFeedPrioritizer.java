package com.example.mova.feed;

import com.example.mova.components.Component;
import com.example.mova.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PersonalFeedPrioritizer extends Prioritizer<Post> {

    public PersonalFeedPrioritizer() {
        super();
        // TODO: Write priorities
    }

    @Override
    public float priorityOf(Post item) {
        return 0;
    }

    public List<Prioritized<Component>> makeSpecialCards() {
        List<Prioritized<Component>> cards = new ArrayList<>();
        /* TODO:
         * - Get all conditional values
         *   - Time of day
         *   - Num logins (possibly hard to access--maybe don't?)
         *   - Num journal entries for the day
         * - Journal prompt
         *   - Do a cost-benefit analysis of time of day vs. whether the user has journaled yet
         *   - If worth it, add card with high priority
         * - Priority prompt
         *   - If night, add card with high priority
         */
        return cards;
    }
}
