package com.example.beassistant.fragments.videos;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.beassistant.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

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

        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        fullScreenViewContainer = (FrameLayout) view.findViewById(R.id.fullscreen_view_container);

        getDataFromLastFragment();

    }

    private void getVideo(String videoId) {

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
            getVideo(videoId);
            return;
        }

        getParentFragmentManager().setFragmentResultListener("video", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Obtain the follower id
                videoId = result.getString("videoId");
                Log.d("Youtube: ", videoId);
                getVideo(videoId);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}