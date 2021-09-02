package com.debugsire.dadjokes.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.debugsire.dadjokes.Model.DownloadsModel;
import com.debugsire.dadjokes.R;
import com.debugsire.dadjokes.Realms.MyDownloads;
import com.debugsire.dadjokes.Realms.MyFavorites;
import com.debugsire.dadjokes.WebService.DAD_JOKES;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment -- ";
    //
    TextView joke;
    LottieAnimationView loader, fav;
    ExtendedFloatingActionButton download, getNew;
    JsonObject jokeJsonObject;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initCompos(root);
        eventHandlers(root);
        loadJoke();
        return root;
    }

    private void eventHandlers(View root) {
        getNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadJoke();
            }
        });

        root.findViewById(R.id.efab_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.putExtra(android.content.Intent.EXTRA_TEXT, jokeJsonObject.get("joke").getAsString().trim());
                startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
            }
        });


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MyFavorites myFavorites = Realm.getDefaultInstance().where(MyFavorites.class)
                                .equalTo("key", jokeJsonObject.get("id").getAsString())
                                .findFirst();
                        if (myFavorites == null) {
                            myFavorites = new MyFavorites();
                            myFavorites.setKey(jokeJsonObject.get("id").getAsString().trim());
                            myFavorites.setJoke(jokeJsonObject.get("joke").getAsString().trim());
                            realm.insert(myFavorites);
                        } else {
                            myFavorites.deleteFromRealm();
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (Realm.getDefaultInstance().where(MyFavorites.class)
                                .equalTo("key", jokeJsonObject.get("id").getAsString())
                                .findFirst() == null) {
                            fav.setFrame((int) fav.getMinFrame());
                        } else {
                            fav.playAnimation();
                        }
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Log.d(TAG, "onError: " + error.getMessage());
                    }
                });
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MyDownloads myDownloads = new MyDownloads();
                        myDownloads.setKey(jokeJsonObject.get("id").getAsString().trim());
                        myDownloads.setJoke(jokeJsonObject.get("joke").getAsString().trim());
                        //
                        realm.insert(myDownloads);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        enableDownloadButton(false);
//                        download.setVisibility(View.GONE);
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Log.d(TAG, "onError: " + error.getMessage());
                    }
                });
            }
        });
    }

    private void loadJoke() {
        getNew.setClickable(false);
        loader.setVisibility(View.VISIBLE);
        fav.setFrame((int) fav.getMinFrame());
        enableDownloadButton(true);
        getAPI().getJoke().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    jokeJsonObject = response.body();
                    joke.setText(jokeJsonObject.get("joke").getAsString().trim());
                    //
                    loader.setVisibility(View.GONE);
                    getNew.setClickable(true);
                    if (Realm.getDefaultInstance().where(MyFavorites.class)
                            .equalTo("key", jokeJsonObject.get("id").getAsString())
                            .findFirst() != null) {
                        fav.setFrame((int) fav.getMaxFrame());
                    }
                    if (Realm.getDefaultInstance().where(MyDownloads.class)
                            .equalTo("key", jokeJsonObject.get("id").getAsString())
                            .findFirst() != null) {
                        enableDownloadButton(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableDownloadButton(boolean enable) {
        download.setEnabled(enable);
        if (enable) {
            download.setAlpha(1);
        } else {
            download.setAlpha(0);
        }
    }

    private void initCompos(View root) {
        joke = root.findViewById(R.id.tv_joke);
        loader = root.findViewById(R.id.lottie_loader);
        fav = root.findViewById(R.id.lottie_fav);
        getNew = root.findViewById(R.id.fab_getNew);
        download = root.findViewById(R.id.efab_download);
    }


    public DAD_JOKES getAPI() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl("https://icanhazdadjoke.com/")
                .build();
        Log.d(TAG, "getAPI: " + retrofit.baseUrl());
        DAD_JOKES dad_jokes = retrofit.create(DAD_JOKES.class);

        return dad_jokes;
    }

    //
    //
    //
    //
    //
    //Event bus initialization

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object o) {
        if (o instanceof String && jokeJsonObject != null) {
            String key = (String) o;
            if (key.equalsIgnoreCase(jokeJsonObject.get("id").getAsString().trim()))
                fav.setFrame((int) fav.getMinFrame());
        } else if (o instanceof DownloadsModel && jokeJsonObject != null) {
            DownloadsModel downloadsModel = (DownloadsModel) o;
            if (downloadsModel.getKey().equalsIgnoreCase(jokeJsonObject.get("id").getAsString().trim()))
                enableDownloadButton(true);
        }
    }

    ;
}