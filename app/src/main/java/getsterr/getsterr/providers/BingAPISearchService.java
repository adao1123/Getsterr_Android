package getsterr.getsterr.providers;

import getsterr.getsterr.models.bing.BingResult;
import getsterr.getsterr.utilities.UtilityFunctions;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by samsiu on 11/21/16.
 */

public class BingAPISearchService {

    public static final String API_URL = "https://api.cognitive.microsoft.com/bing/v5.0/";

    public interface BingSearchRx {
        @GET("search")
        Observable<BingResult> getBingAPIResult(@Query("q") String query,
                                                @Query("count") int count,
                                                @Query("offset") int offset,
                                                @Query("mkt") String market,
                                                @Query("safesearch") String safesearch,
                                                @Query("subscription-key") String key);
    }

    public static BingSearchRx createRx(){
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(UtilityFunctions.getOkHttpLoggingClient())      //For debugging
                .build()
                .create(BingSearchRx.class);
    }
}
