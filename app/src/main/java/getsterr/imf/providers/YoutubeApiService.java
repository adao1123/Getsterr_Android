package getsterr.imf.providers;

import getsterr.imf.models.youtube.YoutubeObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by adao1 on 12/7/2016.
 */

public interface YoutubeApiService {
    @GET("/youtube/v3/search?part=snippet&type=video&maxResults=25&key=AIzaSyDo4cb_5fSA-FHWe1txfH7KfaD8KsqDZB8")
    Call<YoutubeObject> getYoutubeSearch(@Query("q") String query);

}
