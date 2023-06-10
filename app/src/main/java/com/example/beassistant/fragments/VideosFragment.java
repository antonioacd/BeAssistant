package com.example.beassistant.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beassistant.R;
import com.example.beassistant.adapters.VideosRecyclerAdapter;
import com.example.beassistant.models.YoutubeData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class VideosFragment extends Fragment {

    private static String GOOGLE_YOUTUBE_KEY = "AIzaSyABYNXEFSjLiKEn5GdNPwppfQR1r51w94g";
    private static String CHANNEL_ID = "UCio2lnOtW4ZYPBZqhfxXheA";
    private static String CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&q=gatos&maxResults=20&key="+GOOGLE_YOUTUBE_KEY;

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

        recAdapter = new VideosRecyclerAdapter(getContext());

        new RequestYoutubeAPI().execute();

    }

    // Create an async task to get the data from youtube
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNEL_GET_URL);

            Log.d("Youtube", CHANNEL_GET_URL);

            try{
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return  json;
            }catch (IOException e){
                Log.d("Youtube", "Falla01: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    parseVicveosListFromResponse(jsonObject);

                    Log.d("Youtube", jsonObject.toString());
                } catch (Exception e) {
                    Log.d("Youtube", "Falla: " + e.getMessage());
                }
            }
        }
    }

    private void parseVicveosListFromResponse(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has("items")){
            Log.d("Youtube", "Items: " + jsonObject.toString());
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    if (json.has("id")){
                        JSONObject jsonId = json.getJSONObject("id");
                        if (jsonId.has("kind")){
                            if (jsonId.getString("kind").equals("youtube#video")){
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String videId = jsonId.getString("videoId");
                                String title = jsonSnippet.getString("title");
                                String desc = jsonSnippet.getString("description");
                                String published = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                YoutubeData youtubeData = new YoutubeData(videId, title, desc, published, thumbnail);

                                Log.d("Youtube", youtubeData.toString());

                                recAdapter.videosList.add(youtubeData);
                                recAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }catch (Exception e){
                Log.d("Youtube Fallo", e.getMessage());
            }
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

        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;

                // Get the index
                index = rV.getChildAdapterPosition(view);

                String selectedUserId = recAdapter.videosList.get(index).getVideoId();

                Fragment fragment = new PlayerFragment();

                Bundle args = new Bundle();
                args.putString("videoId", selectedUserId);

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.setFragmentResult("video", args);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }
}