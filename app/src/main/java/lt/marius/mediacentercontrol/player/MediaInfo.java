package lt.marius.mediacentercontrol.player;

/**
 * Created by marius-pc on 11/1/15.
 */
public class MediaInfo {

    public String artist;
    public String title;
    public String fileName;

    public int length;  //song length

    MediaInfo(String title, String artist, String fileName) {
        this.title = title;
        this.artist = artist;
        this.fileName = fileName;
    }
}
