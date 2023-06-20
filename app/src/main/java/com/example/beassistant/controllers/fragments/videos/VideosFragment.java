package com.example.beassistant.controllers.fragments.videos;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
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

    private String CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&q=&maxResults=20&key=" + GOOGLE_YOUTUBE_KEY;

    private VideosRecyclerAdapter recAdapter;

    private RecyclerView rV;

    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recAdapter = new VideosRecyclerAdapter(getContext());

        // Get the data from last fragment
        getDataFroPreviousFragment();

    }

    /**
     * Function to get the data from the last fragment
     */
    private void getDataFroPreviousFragment() {

        getParentFragmentManager().setFragmentResultListener("videosFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                String name = result.getString("productName").trim();
                name = name.replaceAll(" ", "+");

                // Actualizar la URL de la API de YouTube con el nombre del producto
                CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&q=" + name + "&maxResults=50&key=" + GOOGLE_YOUTUBE_KEY;

                new RequestYoutubeAPI().execute();
            }
        });
    }

    // Create an async task to get the data from YouTube
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Create an HTTP client and HTTP GET request
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNEL_GET_URL);

            try {
                // Execute the HTTP GET request
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                // Convert the response to a string
                String json = EntityUtils.toString(httpEntity);
                return json;
            } catch (IOException e) {
                Toast.makeText(getContext(), "Ha habido un error", Toast.LENGTH_SHORT);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null) {
                try {
                    // Parse the JSON response into a JSONObject
                    JSONObject jsonObject = new JSONObject(response);

                    // Call the method to parse the videos list from the response
                    parseVideosListFromResponse(jsonObject);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Ha habido un error", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    /**
     * Function to parse videos from response
     * @param jsonObject
     * @throws JSONException
     */
    private void parseVideosListFromResponse(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has("items")) {
            try {
                // Get the "items" JSONArray from the JSON response
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    // Iterate through each item in the array
                    JSONObject json = jsonArray.getJSONObject(i);

                    if (json.has("id")) {
                        // Get the "id" JSON object
                        JSONObject jsonId = json.getJSONObject("id");
                        if (jsonId.has("kind")) {
                            // Check if the "kind" value is "youtube#video"
                            if (jsonId.getString("kind").equals("youtube#video")) {
                                // Get the video details from the "snippet" JSON object
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String videoId = jsonId.getString("videoId");
                                String title = jsonSnippet.getString("title");
                                String desc = jsonSnippet.getString("description");
                                String published = "Published on: " + jsonSnippet.getString("publishedAt").substring(0, 10);
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                // Create a YoutubeData object with the video details
                                YoutubeData youtubeData = new YoutubeData(videoId, title, desc, published, thumbnail);

                                // Add the YoutubeData object to the videosList and notify the adapter
                                recAdapter.videosList.add(youtubeData);
                                recAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Ha habido un error", Toast.LENGTH_SHORT);
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

                // Get the selected user id
                String selectedUserId = recAdapter.videosList.get(index).getVideoId();

                // Crete the fragment
                Fragment fragment = new PlayerFragment();

                // Put the arguments
                Bundle args = new Bundle();
                args.putString("videoId", selectedUserId);

                // Set the fragment
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