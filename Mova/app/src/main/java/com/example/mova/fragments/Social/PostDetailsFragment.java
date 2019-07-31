package com.example.mova.fragments.Social;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.components.PostComponent;
import com.example.mova.model.Post;
import com.example.mova.scrolling.EdgeDecorator;
import com.example.mova.scrolling.EndlessScrollLayout;
import com.example.mova.scrolling.ScrollLoadHandler;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POST = "post";
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.clPost)      protected ComponentLayout clPost;
    @BindView(R.id.tvComments)  protected TextView tvComments;
    @BindView(R.id.eslComments) protected EndlessScrollLayout<Component.ViewHolder> eslComments;

    private Post post;

    private List<Post> comments;
    private DataComponentAdapter<Post> adapter;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post The post to display.
     * @return A new instance of fragment PostDetailsFragment.
     */
    public static PostDetailsFragment newInstance(Post post) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = getArguments().getParcelable(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        displayPost();
        configureComments();
    }

    private void displayPost() {
        PostComponent component = new PostComponent(post, new PostComponent.Config(null, true, false));
        clPost.setMargin(32);
        clPost.inflateComponent((DelegatedResultActivity) getActivity(), component);
    }

    private void hideComments() {
        tvComments.setVisibility(View.GONE);
        eslComments.setVisibility(View.GONE);
    }

    private void showComments() {
        tvComments.setVisibility(View.VISIBLE);
        eslComments.setVisibility(View.VISIBLE);
    }

    private void configureComments() {
        hideComments();
        comments = new ArrayList<>();
        adapter = new DataComponentAdapter<Post>((DelegatedResultActivity) getActivity(), comments) {
            @Override
            protected Component makeComponent(Post item, Component.ViewHolder holder) {
                PostComponent.Config config = new PostComponent.Config();
                config.onReply = (savedPost) -> {
                    comments.add(savedPost);
                    adapter.notifyItemInserted(comments.size() - 1);
                };
                return new PostComponent(item);
            }

            @Override
            protected Component.Inflater makeInflater(Post item) {
                return new PostComponent.Inflater();
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        eslComments.init(new ScrollLoadHandler<Component.ViewHolder>() {
            @Override
            public void load() {
                loadComments();
            }

            @Override
            public void loadMore() {
                loadMoreComments();
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

        eslComments.addItemDecoration(new EdgeDecorator(32));

        loadComments();
    }

    private void loadComments() {
        comments.clear();
        adapter.notifyDataSetChanged();
        loadMoreComments();
    }

    private void loadMoreComments() {
        ParseQuery<Post> query = post.relComments.getQuery();
        Post.includeAllPointers(query);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.setLimit(20);
        if (comments.size() > 0) {
            query.whereLessThan(Post.KEY_CREATED_AT, comments.get(comments.size() - 1));
        }

        query.findInBackground((queriedComments, e) -> {
            if (e != null) {
                hideComments();
                Log.e("PostDetailsFragment", "Failed to load comments", e);
                Toast.makeText(getActivity(), "Failed to load comments", Toast.LENGTH_LONG).show();
            } else {
                int endPos = comments.size();
                comments.addAll(queriedComments);
                adapter.notifyItemRangeInserted(endPos, comments.size());
                if (comments.size() > 0) {
                    showComments();
                }
            }
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
