package dk.easj.anbo.retrofitexample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MINE";
    private TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageView = findViewById(R.id.mainMessageTextView);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.mainSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getAndShowData();
            swipeRefreshLayout.setRefreshing(false); // early??
        });
    }

    private void getAndShowData() {
        EditText usernameView = findViewById(R.id.mainUsernameEditText);
        String username = usernameView.getText().toString().trim();

        if (username.length() == 0) {
            usernameView.setError("No input");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                // https://futurestud.io/tutorials/retrofit-2-adding-customizing-the-gson-converter
                // Gson is no longer the default converter
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubService service = retrofit.create(GitHubService.class);

        Call<User> userCall = service.getUser(username);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // onResponse runs on main/UI thread in Android (not in JVM)
                // https://square.github.io/retrofit/2.x/retrofit/retrofit2/Callback.html
                TextView nameView = findViewById(R.id.mainNameTextView);
                TextView companyView = findViewById(R.id.mainCompanyTextView);
                TextView locationView = findViewById(R.id.mainLocationTextView);
                if (response.isSuccessful()) {
                    String message = response.message();
                    User user = response.body();
                    Log.d(LOG_TAG, message + " " + user);
                    nameView.setText(user.getName());
                    companyView.setText(user.getCompany());
                    locationView.setText(user.getLocation());
                    messageView.setText("");
                } else { // response code not 2xx
                    nameView.setText("");
                    companyView.setText("");
                    locationView.setText("");
                    if (response.code() == 404) {
                        messageView.setText("No such user: " + username);
                    } else {
                        messageView.setText(String.format("Not working %d %s", response.code(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) { // network problems
                Log.e(LOG_TAG, t.getMessage());
                messageView.setText(t.getMessage());
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

    public void getInformationClicked(View view) {
        getAndShowData();
    }
}
