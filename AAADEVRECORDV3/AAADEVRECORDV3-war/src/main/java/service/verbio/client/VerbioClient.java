/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.verbio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.json.JSONObject;

import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;

import service.verbio.bean.Usuario;

/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "VerbioClient", urlPatterns = {"/VerbioClient"})
public class VerbioClient extends HttpServlet {

	private final Logger logger = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        Part requestVerbio = request.getPart("request");
        String requestVerbiotString = getStringValue(requestVerbio);
        response.setContentType("application/json");
        HttpSession session = request.getSession(true);
        Usuario user = (Usuario) session.getAttribute("userActive");

        if (requestVerbiotString.equals("VERIFY")) {
            Part base64Audio = request.getPart("base64Audio");
            String base64AudioString = getStringValue(base64Audio);

            try {
                json = verbioVerify(base64AudioString, user.getVerbiouser());
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }
        if (requestVerbiotString.equals("ADD_FILE")) {
            Part base64Audio = request.getPart("base64Audio");
            String base64AudioString = getStringValue(base64Audio);

            try {
                json = verbioAddFile(base64AudioString, user.getVerbiouser());
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }
        if (requestVerbiotString.equals("TRAIN")) {
            try {
            	
            	
            	
                json = verbioTrain(user.getVerbiouser());
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }
        if (requestVerbiotString.equals("USER_INFO")) {
            try {
                json = verbioUserInfo(user.getVerbiouser());
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }

    }

    public JSONObject verbioVerify(String base64Audio, String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"" + base64Audio + "\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"VERIFY\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"0\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";
        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;

    }

    public JSONObject verbioAddFile(String base64Audio, String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"" + base64Audio + "\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"ADD_FILE\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"0\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";

        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;
    }

    public JSONObject verbioTrain(String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"TRAIN\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";

        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;
    }

    public JSONObject verbioUserInfo(String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"USER_INFO\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"0\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";
        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;
    }

    private String getStringValue(final Part part) {

        BufferedReader bufferedReader = null;
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        final String partName = part.getName();
        try {
            final InputStream inputStream = part.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (final IOException e) {
            logger.info("getStringValue - IOException while reading inputStream. Part name : " + partName);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    logger.info("getStringValue - IOException while closing bufferedReader. Part name : " + partName);
                }
            }
        }
        return stringBuilder.toString();
    }

}
