package AAADEVRECORDV3.Bean;

/**
*
* @author umansilla
*/
public class DirectoryAudios {

   private String directoryName;
   private String lastModification;
   private LanguagesDirectories subCarpetas;

   public DirectoryAudios(String directoryName) {
       this.directoryName = directoryName;
   }

   public DirectoryAudios(String directoryName, String lastModification) {
       this.directoryName = directoryName;
       this.lastModification = lastModification;
   }

   public DirectoryAudios(String directoryName, String lastModification, LanguagesDirectories subCarpetas) {
       this.directoryName = directoryName;
       this.lastModification = lastModification;
       this.subCarpetas = subCarpetas;
   }

   public LanguagesDirectories getSubCarpetas() {
       return subCarpetas;
   }

   public void setSubCarpetas(LanguagesDirectories subCarpetas) {
       this.subCarpetas = subCarpetas;
   }

   public String getLastModification() {
       return lastModification;
   }

   public void setLastModification(String lastModification) {
       this.lastModification = lastModification;
   }

   public String getDirectoryName() {
       return directoryName;
   }

   public void setDirectoryName(String directoryName) {
       this.directoryName = directoryName;
   }
}
