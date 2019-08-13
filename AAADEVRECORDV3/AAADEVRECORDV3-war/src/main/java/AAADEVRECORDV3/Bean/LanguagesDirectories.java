package AAADEVRECORDV3.Bean;
/**
 *
 * @author umansilla
 */
public class LanguagesDirectories {
    private EnglishDirectory En;
    private SpanishDirectory Es;
    private PortuguesDirectory Pt;
    private String image;
    
    public LanguagesDirectories() {
    }
    
    public LanguagesDirectories(EnglishDirectory En, SpanishDirectory Es, PortuguesDirectory Pt) {
        this.En = En;
        this.Es = Es;
        this.Pt = Pt;
    }    

    public LanguagesDirectories(EnglishDirectory En, SpanishDirectory Es, PortuguesDirectory Pt, String image) {
        this.En = En;
        this.Es = Es;
        this.Pt = Pt;
        this.image = image;
    }
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    
    
    public EnglishDirectory getEn() {
        return En;
    }

    public void setEn(EnglishDirectory En) {
        this.En = En;
    }

    public SpanishDirectory getEs() {
        return Es;
    }

    public void setEs(SpanishDirectory Es) {
        this.Es = Es;
    }

    public PortuguesDirectory getPt() {
        return Pt;
    }

    public void setPt(PortuguesDirectory Pt) {
        this.Pt = Pt;
    }


}
