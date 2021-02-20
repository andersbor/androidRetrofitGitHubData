package dk.easj.anbo.retrofitexample;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface GitHubService {
    @GET("users/{user}")
    Call<User> getUser(@Path("user") String user);


}
