package com.example.mova.fragments.Social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.ComposePostDialog;
import com.example.mova.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.SearchActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.PostComponent;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.scrolling.EdgeDecorator;
import com.example.mova.scrolling.EndlessScrollRefreshLayout;
import com.example.mova.scrolling.ScrollLoadHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocialFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocialFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFeedFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.fabCompose) protected FloatingActionButton fabCompose;
    @BindView(R.id.esrlPosts)  protected EndlessScrollRefreshLayout<Component.ViewHolder> esrlPosts;
    @BindView(R.id.ibSearch) protected ImageButton ibSearch;

    List<Post> posts;
    DataComponentAdapter<Post> adapter;

    public SocialFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SocialFeedFragment.
     */
    // TODO: Rename and change types and count of parameters
    public static SocialFeedFragment newInstance(String param1, String param2) {
        SocialFeedFragment fragment = new SocialFeedFragment();
        Bundle args = new Bundle();
        // TODO: Set desired params
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Set desired params
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ibSearch ,"search");
                startActivity(intent, options.toBundle());
            }
        });
        // Temporary fabCompose press for testing ComposePostComponent
        fabCompose.setOnClickListener((v) -> {
            ComposePostDialog dialog = new ComposePostDialog((DelegatedResultActivity) getActivity()) {
                @Override
                protected void onCancel() {

                }

                @Override
                protected void onPost(PostConfig config) {
                    config.post.savePost(config, (savedPost) -> addPost(savedPost));
                }
            };
            dialog.show();
        });

        posts = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new DataComponentAdapter<Post>((DelegatedResultActivity) getActivity(), posts) {
            @Override
            public Component makeComponent(Post item, Component.ViewHolder holder) {
                PostComponent.Config config = new PostComponent.Config();
                config.onRepost = (savedPost) -> addPost(savedPost);
                return new PostComponent(item);
            }

            @Override
            protected Component.Inflater makeInflater(Post item) {
                return new PostComponent.Inflater();
            }
        };

        esrlPosts.init(new ScrollLoadHandler<Component.ViewHolder>() {
            @Override
            public void load() {
                loadPosts();
            }

            @Override
            public void loadMore() {
                loadMorePosts();
            }

            @Override
            public RecyclerView.Adapter<Component.ViewHolder> getAdapter() {
                return adapter;
            }

            @Override
            public RecyclerView.LayoutManager getLayoutManager() {
                return layoutManager;
            }

            @Override
            public int[] getColorScheme() {
                return getDefaultColorScheme();
            }
        });

        esrlPosts.addItemDecoration(new EdgeDecorator(32));

        loadPosts();
    }

    private void addPost(Post post) {
        posts.add(0, post);
        adapter.notifyItemInserted(0);
        esrlPosts.scrollToPosition(0);
    }

    private void loadPosts() {
        posts.clear();
        adapter.notifyDataSetChanged();
        loadMorePosts();
    }

    private void loadMorePosts() {
        // Find the user's friends to retrieve their posts
        ParseQuery<User> friendsQuery = User.getCurrentUser().relFriends.getQuery();
        friendsQuery.findInBackground((friends, e) -> {
            if (e != null) {
                Log.e("SocialFeedFragment", "Failed to load friends for feed loading", e);
                Toast.makeText(getActivity(), "Failed to load posts", Toast.LENGTH_LONG).show();
                esrlPosts.setRefreshing(false);
                return;
            }

            // Find the user's groups to retrieve their posts
            ParseQuery<Group> groupsQuery = User.getCurrentUser().relGroups.getQuery();
            groupsQuery.findInBackground((groups, e1) -> {
                if (e1 != null) {
                    Log.e("SocialFeedFragment", "Failed to load groups for feed loading", e1);
                    Toast.makeText(getActivity(), "Failed to load posts", Toast.LENGTH_LONG).show();
                    esrlPosts.setRefreshing(false);
                    return;
                }

                // Make query for user's and friends' posts
                friends.add(User.getCurrentUser());
                ParseQuery<Post> userPostQuery = new ParseQuery<>(Post.class);
                userPostQuery.whereContainedIn(Post.KEY_AUTHOR, friends);

                // Make query for groups' posts
                ParseQuery<Post> groupPostQuery = new ParseQuery<>(Post.class);
                groupPostQuery.whereContainedIn(Post.KEY_GROUP, groups);

                // Combine queries
                ArrayList<ParseQuery<Post>> queries = new ArrayList<>();
                queries.add(userPostQuery);
                queries.add(groupPostQuery);
                ParseQuery<Post> compoundQuery = ParseQuery.or(queries);
                Post.includeAllPointers(compoundQuery);
                compoundQuery.whereEqualTo(Post.KEY_IS_PERSONAL, false); // Only social posts
                compoundQuery.whereEqualTo(Post.KEY_PARENT_POST, JSONObject.NULL); // No replies
                compoundQuery.orderByDescending(Post.KEY_CREATED_AT);
                compoundQuery.setLimit(50); // Arbitrarily higher limit for now because longer loading time to get to this point

                if (posts.size() > 0) {
                    // Posts older than the oldest post currently loaded
                    compoundQuery.whereLessThan(Post.KEY_CREATED_AT, posts.get(posts.size() - 1));
                }

                compoundQuery.findInBackground((fetchedPosts, e2) -> {
                    if (e2 != null) {
                        Log.e("SocialFeedFragment", "Failed to load posts from compound query", e2);
                        Toast.makeText(getActivity(), "Failed to load posts", Toast.LENGTH_LONG).show();
                        esrlPosts.setRefreshing(false);
                        return;
                    }

                    int endPos = posts.size();
                    posts.addAll(fetchedPosts);
                    adapter.notifyItemRangeInserted(endPos, posts.size());
                    esrlPosts.setRefreshing(false);
                });
            });
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
