package dk.easj.anbo.retrofitexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MINE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                // https://futurestud.io/tutorials/retrofit-2-adding-customizing-the-gson-converter
                // Gson is no longer the default converter
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubService service = retrofit.create(GitHubService.class);

        Call<User> userCall = service.getUser("andersbor");
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // Runs on main/UI thread in Android (not in JVM)
                // https://square.github.io/retrofit/2.x/retrofit/retrofit2/Callback.html
                if (response.isSuccessful()) {
                    String message = response.message();
                    User user = response.body();
                    Log.d(LOG_TAG, message + " " + user);
                    TextView nameView = findViewById(R.id.mainNameTextView);
                    TextView companyView = findViewById(R.id.mainCompanyTextView);
                    nameView.setText(user.getName());
                    companyView.setText(user.getCompany());
                } else { // response code not 2xx
                    TextView view = findViewById(R.id.mainMessageTextView);
                    view.setText(String.format("Not working %d %s", response.code(), response.message()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) { // network problems
                Log.e(LOG_TAG, t.getMessage());
                // Example: Unable to resolve host "api.github.comkk": No address associated with hostname
            }
        });
        /*
        try {
            Response<User> response = user.execute();
            String message = response.message();
            Log.d("MINE", message);
        } catch (IOException e) {
            Log.d("MINE", e.getMessage());
        }
        */

    }
}
