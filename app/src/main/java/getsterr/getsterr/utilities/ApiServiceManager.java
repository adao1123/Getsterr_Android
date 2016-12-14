package getsterr.getsterr.utilities;

import getsterr.getsterr.providers.InstagramApiService;
import getsterr.getsterr.providers.YoutubeApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adao1 on 12/7/2016.
 */

public class ApiServiceManager {

    public static InstagramApiService createInstagramApiService(){
        return getRetrofit(Constants.INSTAGRAM_BASE_URL).create(InstagramApiService.class);
    }

    public static YoutubeApiService createYoutubeApiService(){
        return getRetrofit(Constants.YOUTUBE_BASE_URL).create(YoutubeApiService.class);
    }

    private static Retrofit getRetrofit(String baseUrl){
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }
}
