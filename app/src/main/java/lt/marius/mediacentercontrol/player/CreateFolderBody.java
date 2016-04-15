package lt.marius.mediacentercontrol.player;

/**
 * Created by marius on 16.2.21.
 */
public class CreateFolderBody {

    public final String dir;
    public final String name;

    public CreateFolderBody(String dir, String name) {
        this.dir = dir;
        this.name = name;
    }
}
