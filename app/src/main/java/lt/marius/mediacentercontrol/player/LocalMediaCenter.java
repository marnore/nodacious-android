package lt.marius.mediacentercontrol.player;

import lt.marius.mediacentercontrol.player.requests.ActionResp;
import lt.marius.mediacentercontrol.player.requests.CreateFolderResp;
import lt.marius.mediacentercontrol.player.requests.PlaylistResp;
import lt.marius.mediacentercontrol.player.requests.SongsResp;
import lt.marius.mediacentercontrol.player.requests.VolumeResp;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by marius-pc on 11/1/15.
 */
public interface LocalMediaCenter {

    String API_URL = "http://192.168.1.7:3000/music_player/";
    //String API_URL = "http://192.168.1.1:3000/music_player/";

    @GET("play")
    Call<ActionResp> playMusic(@Query("song") String toPlay);

    @GET("stop")
    Call<ActionResp> stop();

    @GET("stop")
    Call<ActionResp> pause();

    @GET("prev")
    Call<ActionResp> prev();

    @GET("next")
    Call<ActionResp> next();

    @POST("enqueue")
    Call<ActionResp> enqueue();

    @GET("radio")
    Call<ActionResp> playRadio();

    @GET("volume")
    Call<VolumeResp> volume(@Query("value") int volume);

    @GET("playList")
    Call<PlaylistResp> playList();

    @GET("folders")
    Call<FolderListing> getFolders(@Query("dir") String dir);

    @GET("songs")
    Call<SongsResp> getSongs(@Query("dir") String dir);

    @POST("createFolder")
    Call<CreateFolderResp> createFolder(@Body CreateFolderBody body );
}
