package com.example.mova.fragments.Social;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.PostComponent;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.containers.EndlessScrollRefreshLayout;
import com.example.mova.containers.ScrollLoadHandler;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScrapbookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScrapbookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScrapbookFragment extends Fragment {

    @BindView(R.id.esrlScrapbook) protected EndlessScrollRefreshLayout<Component.ViewHolder> esrlScrapbook;

    private DataComponentAdapter<Post> adapter;
    private List<Post> posts;

    private OnFragmentInteractionListener mListener;

    public ScrapbookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ScrapbookFragment.
     */
    public static ScrapbookFragment newInstance(String param1, String param2) {
        ScrapbookFragment fragment = new ScrapbookFragment();
        Bundle args = new Bundle();
        // TODO: Set necessary args
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Set necessary args
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scrapbook, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        posts = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new DataComponentAdapter<Post>((DelegatedResultActivity) getActivity(), posts) {
            @Override
            public Component makeComponent(Post item, Component.ViewHolder holder) {
                return new PostComponent(item);
            }

            @Override
            protected Component.Inflater makeInflater(Post item) {
                return new PostComponent.Inflater();
            }
        };

        esrlScrapbook.init(new ScrollLoadHandler<Component.ViewHolder>() {
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

        esrlScrapbook.addItemDecoration(new EdgeDecorator(32));

        loadPosts();
    }

    private void loadPosts() {
        posts.clear();
        adapter.notifyDataSetChanged();
        loadMorePosts();
    }

    private void loadMorePosts() {
        ParseQuery<Post> query = User.getCurrentUser().relScrapbook.getQuery();
        query.setLimit(20);
        query.orderByDescending(Post.KEY_CREATED_AT);
        if (posts.size() > 0) {
            // Older than oldest fetched post
            query.whereLessThan(Post.KEY_CREATED_AT, posts.get(posts.size() - 1));
        }
        query.findInBackground((fetchedPosts, e) -> {
            if (e != null) {
                Log.e("ScrapbookFragment", "Failed to load scrapbook posts", e);
                Toast.makeText(getActivity(), "Failed to load scrapbook posts", Toast.LENGTH_LONG).show();
            } else {
                int endPos = posts.size();
                posts.addAll(fetchedPosts);
                adapter.notifyItemRangeInserted(endPos, posts.size());
            }
            esrlScrapbook.setRefreshing(false);
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
