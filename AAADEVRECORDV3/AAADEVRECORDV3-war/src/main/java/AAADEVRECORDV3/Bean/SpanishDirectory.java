package AAADEVRECORDV3.Bean;

import java.util.Map;

/**
 *
 * @author umansilla
 */
public class SpanishDirectory {
    private Map<String, String> audiosES;

    public SpanishDirectory(Map<String, String> audiosES) {
        this.audiosES = audiosES;
    }

    public Map<String, String> getAudiosES() {
        return audiosES;
    }

    public void setAudiosES(Map<String, String> audiosES) {
        this.audiosES = audiosES;
    }
    
}