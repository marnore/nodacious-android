package lt.marius.mediacentercontrol.player.requests;

import java.util.List;

import lt.marius.mediacentercontrol.player.MediaInfo;

/**
 * Created by marius on 16.2.21.
 */
public class PlaylistResp {

    List<MediaInfo> playlist;

    public List<MediaInfo> getPlaylist() {
        return playlist;
    }
}
