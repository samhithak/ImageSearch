package samhithak.com.imagegallery;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ImageQuery {
    private static final int PAGE_SIZE = 30;

    private static final String FLICKR_QUERY = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=4934fb64027c730dc5a365c4c2977727&text=";
    private static final String FLICKR_PAGE_SIZE = "&extras=url_s%2C+owner_name&per_page=";
    private static final String FLICKR_NEXT_PAGE = "&page=";
    private static final String FLICKR_PARAMS = "&sort=relevance&format=json&nojsoncallback=1";


    private final OkHttpClient client = new OkHttpClient();
    private String mURLPath;
    private boolean mIsRefresh;

    public void computeUrl(String query, int pageNumber) {
        mURLPath = FLICKR_QUERY + query + FLICKR_PAGE_SIZE + PAGE_SIZE + FLICKR_NEXT_PAGE + pageNumber + FLICKR_PARAMS;
        mIsRefresh = (pageNumber == 1);
    }

    public void run(final GalleryFragment.RequestListener listener) throws Exception {
        final Request request = new Request.Builder()
                .url(mURLPath)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject element = (JsonObject)parser.parse(response.body().string());

                    JsonElement queryObject = element.get("photos");
                    JsonElement allImagesObject = queryObject.getAsJsonObject().get("photo");

                    Type listType = new TypeToken<List<Image>>(){}.getType();
                    List<Image> images = gson.fromJson(allImagesObject, listType);
                    listener.onSuccess(images, mIsRefresh);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
