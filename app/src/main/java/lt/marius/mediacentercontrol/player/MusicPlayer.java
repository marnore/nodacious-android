package lt.marius.mediacentercontrol.player;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import lt.marius.mediacentercontrol.player.requests.ActionResp;
import lt.marius.mediacentercontrol.player.requests.SongsResp;
import lt.marius.mediacentercontrol.player.requests.VolumeResp;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by marius-pc on 11/1/15.
 */
public class MusicPlayer {

    private static final MusicPlayer INSTANCE = new MusicPlayer();

    public static MusicPlayer getInstance() {
        return INSTANCE;
    }



    private LocalMediaCenter mediaCenter;

    private MusicPlayer() {

        OkHttpClient client = new OkHttpClient();

        client.interceptors().add(new Interceptor() {
              @Override
              public com.squareup.okhttp.Response intercept(Interceptor.Chain chain) throws IOException {
                  Request original = chain.request();

                  Request request = original.newBuilder()
                          .header("Accept", "Application/json")
                          .build();

                  return chain.proceed(request);
              }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LocalMediaCenter.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        mediaCenter = retrofit.create(LocalMediaCenter.class);
    }

    private void enqueueAndLog(Call<ResponseBody> call) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        System.out.println(body.string());
                        return;
                    }
                    body = response.errorBody();
                    if (body != null) {
                        System.err.println(body.string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void play(Callback<ActionResp> callback) {
        mediaCenter.playMusic(null).enqueue(callback);
    }


    public void play(String toPlay, Callback<ActionResp> callback) {
        mediaCenter.playMusic(toPlay).enqueue(callback);
    }

    public void radio(Callback<ActionResp> callback) {
        mediaCenter.playRadio().enqueue(callback);
    }

    public void stop(Callback<ActionResp> callback) {
        mediaCenter.stop().enqueue(callback);
    }

    public void pause(Callback<ActionResp> callback) {
        mediaCenter.stop().enqueue(callback);
    }

    public void next(Callback<ActionResp> callback) {
        mediaCenter.next().enqueue(callback);
    }

    public void prev(Callback<ActionResp> callback) {
        mediaCenter.prev().enqueue(callback);
    }

    public void getFolders(String root, Callback<FolderListing> callback) {
        mediaCenter.getFolders(root).enqueue(callback);
    }

    public void getSongs(String root, Callback<SongsResp> callback) {
        mediaCenter.getSongs(root).enqueue(callback);
    }

    public void setVolume(int volume, Callback<VolumeResp> callback) {
        mediaCenter.volume(volume).enqueue(callback);
    }



    public MediaInfo getInfo() {
        return new MediaInfo("Title", "Artist", "super song.mp3");
    }

}
