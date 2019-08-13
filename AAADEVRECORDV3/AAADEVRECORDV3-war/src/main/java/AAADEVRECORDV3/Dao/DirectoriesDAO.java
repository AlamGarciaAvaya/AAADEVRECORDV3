package AAADEVRECORDV3.Dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AAADEVRECORDV3.Bean.DirectoryAudios;
import AAADEVRECORDV3.Bean.EnglishDirectory;
import AAADEVRECORDV3.Bean.LanguagesDirectories;
import AAADEVRECORDV3.Bean.PortuguesDirectory;
import AAADEVRECORDV3.Bean.SpanishDirectory;
import AAADEVRECORDV3.Interfaz.DirectoryAccess;
import AAADEVRECORDV3.util.Constants;

import com.avaya.collaboration.util.logger.Logger;
/**
 *
 * @author umansilla
 */
public class DirectoriesDAO implements DirectoryAccess {
	private final Logger logger = Logger.getLogger(getClass());
    @Override
    public List<DirectoryAudios> getAllDirectories() throws IOException {
        String dirName = Constants.PATH_TO_AUDIOS;
        logger.info(Constants.PATH_TO_AUDIOS);
        List<DirectoryAudios> directories = new ArrayList<>();
        Files.list(new File(dirName).toPath())
                .forEach(path -> {
                    try {
                        directories.add(new DirectoryAudios(path.getFileName().toString(), getLastModification(path.toString()), getLanguagesDirectory(path.toString(), path.getFileName().toString())));
                    } catch (IOException ex) {
                        logger.error("Error al crear directories: " + ex.toString());
                    }
                });
        return directories;
    }

    private String getLastModification(String path) {
        File file = new File(path);
        long lastModified = file.lastModified();
        String pattern = "yyyy-MM-dd hh:mm aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date lastModifiedDate = new Date(lastModified);
        return simpleDateFormat.format(lastModifiedDate);
    }

    private LanguagesDirectories getLanguagesDirectory(String path, String directory) throws IOException {

        LanguagesDirectories languageDirectories = new LanguagesDirectories();
        Files.list(new File(path).toPath())
                .forEach(pathLanguages -> {
                    if (pathLanguages.getFileName().toString().equals("EN")) {
                        try {
                            EnglishDirectory En = getEnglishAudioFiles(pathLanguages.toString(), directory, pathLanguages.getFileName().toString());
                            languageDirectories.setEn(En);
                        } catch (IOException ex) {
                            logger.error("Error al encontrar archivos del directorio en EN " + ex.toString()) ;
                        }

                    }
                    if (pathLanguages.getFileName().toString().equals("ES")) {
                        try {
                            SpanishDirectory Es = getSpanishAudioFiles(pathLanguages.toString(), directory, pathLanguages.getFileName().toString());
                            languageDirectories.setEs(Es);
                        } catch (IOException ex) {
                        	logger.error("Error al encontrar archivos del directorio en ES " + ex.toString());
                        }

                    }
                    if (pathLanguages.getFileName().toString().equals("PT")) {
                        try {
                            PortuguesDirectory Pt = getPortugueseAudioFiles(pathLanguages.toString(), directory, pathLanguages.getFileName().toString());
                            languageDirectories.setPt(Pt);
                        } catch (IOException ex) {
                        	logger.error("Error al encontrar archivos del directorio en PT " + ex.toString());
                        }
                    }
                    if (pathLanguages.getFileName().toString().equals("IMG")) {
                        try {
                            languageDirectories.setImage(getImage(pathLanguages.toString(), directory, pathLanguages.getFileName().toString()));
                        } catch (IOException ex) {
                        	logger.error("Error al encontrar archivo en IMG " + ex.toString());
                        }
                    }
                });

        return languageDirectories;
    }

    private String getImage(String path, String directoryName, String languajeDirectory) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.list(new File(path).toPath())
                .forEach(imageFile -> {
                    sb.append("Audios/" + directoryName + "/" + languajeDirectory + "/" + imageFile.getFileName().toString());
                });

        return sb.toString();
    }

    private EnglishDirectory getEnglishAudioFiles(String path, String directoryName, String languajeDirectory) throws IOException {
        Map<String, String> map = new HashMap<>();
        Files.list(new File(path).toPath())
                .forEach(englishFiles -> {
                    map.put(englishFiles.getFileName().toString(), "Audios/" + directoryName + "/" + languajeDirectory + "/" + englishFiles.getFileName().toString());
                });
        EnglishDirectory englishFiles = new EnglishDirectory(map);
        return englishFiles;
    }

    private SpanishDirectory getSpanishAudioFiles(String path, String directoryName, String languajeDirectory) throws IOException {
        Map<String, String> map = new HashMap<>();
        Files.list(new File(path).toPath())
                .forEach(spanishFiles -> {
                    map.put(spanishFiles.getFileName().toString(), "Audios/" + directoryName + "/" + languajeDirectory + "/" + spanishFiles.getFileName().toString());
                });
        SpanishDirectory spanishFiles = new SpanishDirectory(map);
        return spanishFiles;
    }

    private PortuguesDirectory getPortugueseAudioFiles(String path, String directoryName, String languajeDirectory) throws IOException {

        Map<String, String> map = new HashMap<>();
        Files.list(new File(path).toPath())
                .forEach(portugueseFiles -> {
                    map.put(portugueseFiles.getFileName().toString(), "Audios/" + directoryName + "/" + languajeDirectory + "/" + portugueseFiles.getFileName().toString());
                });
        PortuguesDirectory portugueseFiles = new PortuguesDirectory(map);
        return portugueseFiles;
    }

}
