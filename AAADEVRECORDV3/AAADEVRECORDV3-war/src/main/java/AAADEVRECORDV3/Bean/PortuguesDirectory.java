package AAADEVRECORDV3.Bean;

import java.util.Map;

/**
 *
 * @author umansilla
 */
public class PortuguesDirectory {
    private Map<String, String> audiosPT;

    public PortuguesDirectory(Map<String, String> audiosPT) {
        this.audiosPT = audiosPT;
    }

    public Map<String, String> getAudiosPT() {
        return audiosPT;
    }

    public void setAudiosPT(Map<String, String> audiosPT) {
        this.audiosPT = audiosPT;
    }
}
