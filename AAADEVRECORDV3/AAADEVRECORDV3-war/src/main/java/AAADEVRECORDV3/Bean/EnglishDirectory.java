package AAADEVRECORDV3.Bean;

import java.util.Map;

/**
 *
 * @author umansilla
 */
public class EnglishDirectory {
    Map<String, String>  audiosEN;

    public EnglishDirectory(Map<String, String> audiosEN) {
        this.audiosEN = audiosEN;
    }

    public Map<String, String> getAudiosEN() {
        return audiosEN;
    }

    public void setAudiosEN(Map<String, String> audiosEN) {
        this.audiosEN = audiosEN;
    }
    
}
