package com.example.beassistant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RecoverySystem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.beassistant.R;
import com.example.beassistant.adapters.OpinionsRecyclerAdapter;
import com.example.beassistant.adapters.VideosRecyclerAdapter;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class VideosFragment extends Fragment {

    private static String GOOGLE_YOUTUBE_KEY = "AIzaSyAkEWW0YA-MGqlixMd_sInCwppNfsmZGNY";
    private static String CHANNEL_ID = "";
    private static String CHANNEL_GET_URL = "AIzaSyAkEWW0YA-MGqlixMd_sInCwppNfsmZGNY";


    // The recicler adapter
    private VideosRecyclerAdapter recAdapter;

    // The recicler view
    private RecyclerView rV;

    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            YouTube youtube = new YouTube.Builder(httpTransport, JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                    request.getHeaders().set("key", GOOGLE_YOUTUBE_KEY);
                }
            }).build();

            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setQ("gatos"
            search.setType("video");
            search.setMaxResults(10L);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResults = searchResponse.getItems();

            for (SearchResult result : searchResults) {
                String videoTitle = result.getSnippet().getTitle();
                String videoId = result.getId().getVideoId();
                Log.d("Youtube", "Video: " + videoTitle + " (ID: " + videoId + ")");
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init the recycler view
        rV = (RecyclerView) view.findViewById(R.id.recyler_view_videos);

        // Create a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // Set the layout manager to the recycler view
        rV.setLayoutManager(layoutManager);

        // Set the recycler adapter in the recycler view
        rV.setAdapter(recAdapter);

    }
}