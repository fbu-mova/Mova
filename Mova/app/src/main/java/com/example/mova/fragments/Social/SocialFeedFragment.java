package com.example.mova.fragments.Social;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mova.ComposePostDialog;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.components.ComponentLayout;
import com.example.mova.components.ComposePostComponent;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;

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

    @BindView(R.id.button) protected Button button; // a very temporary button for testing

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

        // Temporary button press for testing ComposePostComponent
        button.setOnClickListener((v) -> {
            ComposePostDialog dialog = new ComposePostDialog((DelegatedResultActivity) getActivity()) {
                @Override
                protected void onCancel() {

                }

                @Override
                protected void onPost(Post post, List<Tag> tags, Media media, Post postToReply) {
                    post.savePost(postToReply, tags, media, (savedPost) -> {

                    });
                }
            };
            dialog.show();
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
