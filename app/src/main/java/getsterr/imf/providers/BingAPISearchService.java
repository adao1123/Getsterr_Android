package getsterr.imf.providers;

import getsterr.imf.models.bing.BingImageResult;
import getsterr.imf.models.bing.BingResult;
import getsterr.imf.models.bing.BingVideoResult;
import getsterr.imf.utilities.Constants;
import getsterr.imf.utilities.UtilityFunctions;
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


    public interface BingSearchRx {
        @GET("search")
        Observable<BingResult> getBingAPIResult(@Query("q") String query,
                                                @Query("count") int count,
                                                @Query("offset") int offset,
                                                @Query("mkt") String market,
                                                @Query("safesearch") String safesearch,
                                                @Query("subscription-key") String key);
    }

    public interface BingImageRx {
        @GET("search")
        Observable<BingImageResult> getBingAPIResult(@Query("q") String query,
                                                     @Query("count") int count,
                                                     @Query("offset") int offset,
                                                     @Query("mkt") String market,
                                                     @Query("safesearch") String safesearch,
                                                     @Query("subscription-key") String key);
    }

    public interface BingVideoRx {
        @GET("search")
        Observable<BingVideoResult> getBingAPIResult(@Query("q") String query,
                                                     @Query("count") int count,
                                                     @Query("offset") int offset,
                                                     @Query("mkt") String market,
                                                     @Query("safesearch") String safesearch,
                                                     @Query("subscription-key") String key);
    }

    public static BingSearchRx createRx(String apiUrl){
        return new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(UtilityFunctions.getOkHttpLoggingClient())      //For debugging
                .build()
                .create(BingSearchRx.class);
    }

    public static BingImageRx createImageRx(){
        return new Retrofit.Builder()
                .baseUrl(Constants.BING_IMAGE_API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(UtilityFunctions.getOkHttpLoggingClient())
                .build()
                .create(BingImageRx.class);
    }

    public static BingVideoRx createVideoRx(){
        return new Retrofit.Builder()
                .baseUrl(Constants.BING_VIDEO_API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(UtilityFunctions.getOkHttpLoggingClient())
                .build()
                .create(BingVideoRx.class);
    }

}
