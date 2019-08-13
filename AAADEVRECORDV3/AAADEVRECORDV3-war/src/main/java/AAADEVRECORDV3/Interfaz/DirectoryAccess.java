package AAADEVRECORDV3.Interfaz;

import java.io.IOException;
import java.util.List;

import AAADEVRECORDV3.Bean.DirectoryAudios;
/**
 *
 * @author umansilla
 */
public interface DirectoryAccess {
    public List<DirectoryAudios> getAllDirectories() throws IOException;
}
