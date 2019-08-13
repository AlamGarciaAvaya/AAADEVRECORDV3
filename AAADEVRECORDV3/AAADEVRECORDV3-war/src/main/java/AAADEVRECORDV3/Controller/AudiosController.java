package AAADEVRECORDV3.Controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import AAADEVRECORDV3.Actions.CreateDirectory;
import AAADEVRECORDV3.Actions.DeleteFile;
import AAADEVRECORDV3.Actions.UploadFile;
import AAADEVRECORDV3.Dao.DirectoriesDAO;
import AAADEVRECORDV3.util.PartToString;

import com.avaya.collaboration.util.logger.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "AudiosController", urlPatterns = {"/Admin/Audios"})
public class AudiosController extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        
        DirectoriesDAO dao = new DirectoriesDAO();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(dao.getAllDirectories()); //convert 
        out.println(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        setAccessControlHeaders(response);
        try {
            JSONObject json = new JSONObject();
            String actionString = new PartToString().getStringValue(request.getPart("action"));
            switch (actionString) {
                case "uploadFile":
                    UploadFile upload = new UploadFile(request);
                    json = upload.enterUpload();
                    break;
                case "deleteFile":
                    DeleteFile delete = new DeleteFile(request);
                    json = delete.enterDeleteFile();
                    break;
                case "createFile":
                    CreateDirectory createDirectory = new CreateDirectory(request);
                    json = createDirectory.enterCreateDirectory();
                    break;
            }
            response.setContentType("application/json");
            out.println(json);
        } catch (IOException | ServletException e) {
            logger.error("Error AudiosController: " + e.toString());
            out.println(new JSONObject().put("status", "error"));
        }

    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Accept, X-Requested-With");
    }
}
