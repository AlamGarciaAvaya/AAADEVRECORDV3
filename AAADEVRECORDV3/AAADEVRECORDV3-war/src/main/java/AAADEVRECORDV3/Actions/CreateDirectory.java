package AAADEVRECORDV3.Actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.PartToString;

import com.avaya.collaboration.util.logger.Logger;

/**
 *
 * @author umansilla
 */
public class CreateDirectory {
	private final Logger logger = Logger.getLogger(getClass());
    final private HttpServletRequest request;
    private String folder;

    public CreateDirectory(HttpServletRequest request) {
        this.request = request;
    }

    public JSONObject enterCreateDirectory() throws IOException, ServletException {
        logger.info("Create Folder");
        folder = new PartToString().getStringValue(request.getPart("directoryName"));

        final File file = new File(Constants.PATH_TO_AUDIOS + folder);
        if (file.exists()) {
            return new JSONObject().put("status", "the file already exists");
        } else {
            new File(Constants.PATH_TO_AUDIOS + folder).mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "EN").mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "ES").mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "PT").mkdirs();
            new File(Constants.PATH_TO_AUDIOS + folder + "/" + "IMG").mkdirs();
            return new JSONObject().put("status", "ok");
        }
        
    }
}