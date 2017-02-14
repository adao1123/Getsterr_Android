package getsterr.imf.utilities;

import getsterr.imf.providers.GiphyApiService;
import getsterr.imf.providers.InstagramApiService;
import getsterr.imf.providers.YoutubeApiService;
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

    public static GiphyApiService createGiphyApiService(){
        return getRetrofit(Constants.GIPHY_BASE_URL).create(GiphyApiService.class);
    }

    private static Retrofit getRetrofit(String baseUrl){
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }
}
