package com.example.mova;

import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
import com.example.mova.utils.AsyncUtils;

import java.util.ArrayList;
import java.util.List;

public class PostConfig {
    public Post post, postToReply;
    public List<Tag> tags;
    public Media media;
    public boolean isPersonal;

    public PostConfig() {
        this.post = null;
        this.postToReply = null;
        this.tags = new ArrayList<>();
        this.media = null;
        this.isPersonal = false;
    }

    public PostConfig(Post post) {
        this.post = post;
        this.postToReply = null;
        this.tags = new ArrayList<>();
        this.media = null;
        this.isPersonal = false;
    }

    public void savePost(AsyncUtils.ItemCallback<Post> callback) {
        post.setIsPersonal(isPersonal);
        post.savePost(this, callback);
    }
}
