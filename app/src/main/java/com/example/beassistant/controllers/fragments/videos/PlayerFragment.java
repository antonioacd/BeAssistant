package com.example.beassistant.controllers.fragments.videos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.beassistant.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class PlayerFragment extends Fragment {

    private String videoId = "";

    private YouTubePlayerView youTubePlayerView;
    private FrameLayout fullScreenViewContainer;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init the view variables
        initViewVariables(view);

        // Get the data from the last fragment
        getDataFromLastFragment();
    }

    /**
     * Init the view variables
     * @param view
     */
    private void initViewVariables(@NonNull View view) {
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        fullScreenViewContainer = (FrameLayout) view.findViewById(R.id.fullscreen_view_container);
    }

    /**
     * Function to get the video data
     * @param videoId
     */
    private void playVideo(String videoId) {

        // Get the lifecycle
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.setEnableAutomaticInitialization(false);

        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .build();

        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        }, true, iFramePlayerOptions);
    }

    /**
     * Get the data from last fragment
     */
    private void getDataFromLastFragment(){

        if (!videoId.isEmpty()) {
            playVideo(videoId);
            return;
        }

        getParentFragmentManager().setFragmentResultListener("video", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Obtain the video id
                videoId = result.getString("videoId");

                // Play video
                playVideo(videoId);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}