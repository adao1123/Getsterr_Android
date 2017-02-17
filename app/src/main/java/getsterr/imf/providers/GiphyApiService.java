package getsterr.imf.providers;

import getsterr.imf.models.giphy.GiphyObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by adao1 on 2/13/2017.
 */

public interface GiphyApiService {
//    @GET("/v1/gifs/search?api_key=dc6zaTOxFJmzC&limit=100")
    @GET("/v1/gifs/search")
    Call<GiphyObject> getGiphySearch(@Query("q") String query, @Query("api_key") String key);
}
