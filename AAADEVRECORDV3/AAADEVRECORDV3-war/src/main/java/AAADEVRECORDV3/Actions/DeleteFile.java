package AAADEVRECORDV3.Actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.PartToString;

/**
 *
 * @author umansilla
 */
public class DeleteFile {
    final private HttpServletRequest request;
    private String language;
    private String folder;
    private String audioName;

    public DeleteFile(HttpServletRequest request) {
        this.request = request;
    }

    public JSONObject enterDeleteFile() throws IOException, ServletException {
        JSONObject jsonResponse = new JSONObject();
        JSONArray jsonArray = new JSONArray(new PartToString().getStringValue(request.getPart("files_Array")));
        jsonArray.forEach(jsonElements -> {
            JSONObject json = new JSONObject(jsonElements.toString());
            String arr = json.keys().next();
            String[] arrOfStr = arr.split(",");
            language = arrOfStr[0];
            folder = arrOfStr[1];
            audioName = json.getString(arr);
            Boolean valueDeleted = deleteFiles();
            if(valueDeleted){
                jsonResponse.put(audioName, "Deleted");
            }else{
                jsonResponse.put(audioName, "No Deleted");
            }
            
        });
        return new JSONObject().put("status", "ok").put("audios", jsonResponse);
    }
    
    private Boolean deleteFiles(){
        final File file = new File(Constants.PATH_TO_AUDIOS + folder + "/" + language + "/" + audioName);
        if (file.exists() && file.delete()) {
            return true;
        }else {
            return false;
        }
    }
}
