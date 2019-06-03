/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.verbio;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.avaya.collaboration.util.logger.Logger;

import static service.verbio.LogIn.GenerateKeyAndIV;
import static service.verbio.LogIn.readFile;
import service.verbio.bean.Usuario;

/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "UserProperties", urlPatterns = {"/UserProperties"})
public class UserProperties extends HttpServlet {
	

	private final Logger logger = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        Part action = request.getPart("action");
        String actionString = getStringValue(action);
        response.setContentType("application/json");

        HttpSession session = request.getSession(true);
        Usuario user = (Usuario) session.getAttribute("userActive");
        if (user == null) {
            json.put("status", "false");
            out.println(json);
        } else {
            if (actionString.equals("userProp")) {
                json.put("user", user.getUsername());
                json.put("real_name", user.getName());
                json.put("verbio_user", user.getVerbiouser());
                json.put("date", user.getFecha());
                json.put("hour", user.getHora());
                json.put("phone_active", user.getPhone());
                json.put("verbio_train", user.getTrain());

                out.println(json);

            }
            if (actionString.equals("closeSession")) {
                session.invalidate();
                json.put("status", "ok");
                out.println(json);
            }

            if (actionString.equals("createVerbio")) {
                Part userVerbio = request.getPart("userVerbio");
                Part phoneActive = request.getPart("phoneActive");
                String userVerbioString = getStringValue(userVerbio);
                String phoneActiveString = getStringValue(phoneActive);
                try {
                    createVerbio(userVerbioString, phoneActiveString, user);
                    user.setVerbiouser(userVerbioString);
                    user.setPhone(phoneActiveString);
                    session.setAttribute("userActive", user);
                    session.setMaxInactiveInterval(15 * 60);
                    json.put("status", "updated");
                } catch (Exception e) {
                    logger.info("UserProperties Error: " + e.toString());
                    json.put("error", "error");
                }
                out.println(json);
            }

            if (actionString.equals("saveSettings")) {
                Part name = request.getPart("name");
                Part phone = request.getPart("phone");
                Part country = request.getPart("country");
                Part password = request.getPart("password");

                String nameString = getStringValue(name);
                String phoneString = getStringValue(phone);
                String countryString = getStringValue(country);
                String passwordString = getStringValue(password);
                String deciptString = decryptText(passwordString, "secret");

                try {
                    updateSettings(nameString, phoneString, countryString, deciptString, user);
                    if (nameString.equals("") == false) {
                        user.setName(nameString);
                    }
                    if (phoneString.equals("") == false) {
                        user.setPhone(phoneString);
                    }
                    if (countryString.equals("") == false) {
                        user.setCountry(countryString);
                    }
                    session.setAttribute("userActive", user);
                    session.setMaxInactiveInterval(15 * 60);
                    json.put("status", "updated");
                } catch (IOException | JSONException e) {
                    logger.info("UserProperties Error: " + e.toString());
                    json.put("error", "error");
                }
                out.println(json);
            }

        }

    }

    public void updateSettings(String nameString, String phoneString, String countryString, String deciptString, Usuario user) throws IOException {
        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {

                if (nameString.equals("") == false) {
                    jobj.getJSONObject(i).put("name", nameString);
                }
                if (phoneString.equals("") == false) {
                    jobj.getJSONObject(i).put("phone", phoneString);
                }
                if (countryString.equals("") == false) {
                    jobj.getJSONObject(i).put("country", countryString);
                }
                if (deciptString.equals("") == false) {
                    jobj.getJSONObject(i).put("password", deciptString);
                }
                break;
            } else {
                continue;
            }

        }
        FileWriter fichero = new FileWriter("home/wsuser/web/LogIn/Access.txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
    }

    public static String decryptText(String cipherText, String secret) {

        String decryptedText = null;
        byte[] cipherData = java.util.Base64.getDecoder().decode(cipherText);
        byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
            System.out.println("decryptedText success");
            return decryptedText;
        } catch (Exception ex) {
            System.out.println("error on decrypt: " + ex.getMessage());
            return decryptedText;
        }
    }

    public void createVerbio(String userVerbioString, String phoneActiveString, Usuario user) throws IOException {

        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                jobj.getJSONObject(i).put("verbiouser", userVerbioString);
                jobj.getJSONObject(i).put("phone", phoneActiveString);
                jobj.getJSONObject(i).put("train", "yes");
                break;
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter("home/wsuser/web/LogIn/Access.txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
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
