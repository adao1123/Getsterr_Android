package getsterr.getsterr.providers;

import getsterr.getsterr.models.instagram.InstagramResponseObj;
import getsterr.getsterr.models.instagram.InstagramUserResponse;
import getsterr.getsterr.models.instagram.TokenResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by adao1 on 12/7/2016.
 */

public interface InstagramApiService {

    @FormUrlEncoded
    @POST("/oauth/access_token")
    Call<TokenResponse> getAccessToken(@Field("client_id") String client_id, @Field("client_secret") String client_secret,
                                       @Field("redirect_uri") String redirect_uri, @Field("grant_type") String grant_type,
                                       @Field("code") String code);

    @GET("/v1/tags/{tag-name}/media/recent")
    Call<ResponseBody> getResponse(@Path("tag-name") String tagName, @Query("access_token") String accessToken,
                                   @Query("max_id") String maxId, @Query("min_id") String minId);

    @GET("/v1/tags/{tag-name}/media/recent")
    Call<InstagramResponseObj> getInstagramResponse(@Path("tag-name") String tagName, @Query("access_token") String accessToken,
                                                    @Query("max_id") String maxId, @Query("min_id") String minId);

    @GET("/v1/tags/{tag-name}/media/recent")
    Call<InstagramResponseObj> getInstagramResponse(@Path("tag-name") String tagName, @Query("access_token") String accessToken);

    @GET("/v1/users/{user-id}/media/recent")
    Call<InstagramResponseObj> getInstagramUserRecent(@Path("user-id") String userId, @Query("access_token") String accessToken);

    @GET("/v1/users/self/") //https://api.instagram.com/v1/users/self/?access_token=ACCESS-TOKEN
    Call<InstagramUserResponse> getInstagramUserInfo(@Query("access_token") String accessToken);

    @FormUrlEncoded
    @POST("/v1/media/{media-id}/likes")
    Call<ResponseBody> postInstagramLike(@Field("access_token") String accessToken, @Path("media-id") String mediaId );

//    @FormUrlEncoded
//    @POST("/v1/media/{media-id}/likes")
//    Call<ResponseBody> postInstagramLike(@Path("media-id") String mediaId, @Query("access_code") String access_code, @Query("access_token") String accessToken);
}
