package lt.marius.mediacentercontrol.player.requests;

import lt.marius.mediacentercontrol.player.MediaInfo;

/**
 * Created by marius-pc on 11/1/15.
 */
public class ActionResp {

    public static final String STATUS_STOPPED = "stopped";
    public static final String STATUS_PLAYING = "playing";
    public static final String STATUS_PAUSED = "paused";

    public String status;
    public MediaInfo currSong;
    public int currPlayIndex;

}
