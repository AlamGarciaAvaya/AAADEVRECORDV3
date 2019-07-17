/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.verbio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.json.JSONObject;

import com.avaya.collaboration.util.logger.Logger;

import service.verbio.bean.Usuario;

/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "LogIn", urlPatterns = {"/LogIn"})
public class LogIn extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(getClass());
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("GET");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        HttpSession session = request.getSession(true);
        Usuario user = (Usuario) session.getAttribute("userActive");
        if (user == null) {
            json.put("status", "false");
            out.println(json);
        } else {
            json.put("status", "ok");
            out.println(json);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        JSONObject json = new JSONObject();
        Part emailPart = request.getPart("Email");
        String emailString = getStringValue(emailPart);
        logger.info(emailString);
        Part pass = request.getPart("Pass");
        String passString = getStringValue(pass);
        response.setContentType("application/json");
        Pattern pat = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mat = pat.matcher(emailString);

        if (mat.find()) {
            if (validateUserAndPass(emailString, decryptText(passString, "secret"), request)) {
                response.setStatus(200);
                json.put("status", "ok");
                logger.info("True");
                out.println(json);
            } else {
                response.setStatus(400);
                logger.info("False");
                json.put("status", "false");
                out.println(json);
            }
        } else {
            response.setStatus(400);
            json.put("status", "invalidEmail");
            out.println(json);
        }

    }

    public Boolean validateUserAndPass(String email, String password, HttpServletRequest request) {
//        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        logger.info("validateUserAndPass");

        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        logger.info(jsonData);
        JSONArray jobj = new JSONArray(jsonData);
        logger.info("JSONArray");
        logger.info(jobj);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            String passwordFile = jobj.getJSONObject(i).getString("password");
            if (userName.equals(email) && password.equals(passwordFile)) {
                String name = jobj.getJSONObject(i).has("name") ? jobj.getJSONObject(i).getString("name") : "";
                String verbiouser = jobj.getJSONObject(i).has("verbiouser") ? jobj.getJSONObject(i).getString("verbiouser") : "";
                String fecha = jobj.getJSONObject(i).has("fecha") ? jobj.getJSONObject(i).getString("fecha") : "";
                String hora = jobj.getJSONObject(i).has("hora") ? jobj.getJSONObject(i).getString("hora") : "";
                String phone = jobj.getJSONObject(i).has("phone") ? jobj.getJSONObject(i).getString("phone") : "";
                String train = jobj.getJSONObject(i).has("train") ? jobj.getJSONObject(i).getString("train") : "";
                String country = jobj.getJSONObject(i).has("country") ? jobj.getJSONObject(i).getString("country") : "";
                //MODIFICADO EL 10 de Julio 2019
                Boolean cajaSocialExists = jobj.getJSONObject(i).has("Caja_Social")?true:false;
                String cuenta = "";
                String saldo = "";
                ArrayList<String> históricoList = null;
                if(cajaSocialExists){
                    JSONObject cajaSocial = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                    cuenta = cajaSocial.getString("Cuenta_Caja_Social");
                    saldo = cajaSocial.getString("Saldo_Caja_Social");
                    JSONArray cajaSocialArray = cajaSocial.getJSONArray("Historico_Caja_Social");
                    históricoList = new ArrayList<String>();
                    for (int j = 0; j <= cajaSocialArray.length() - 1; j++) {
                        históricoList.add(cajaSocialArray.getString(j));
                    }
                }
                
                Usuario user = new Usuario(jobj.getJSONObject(i).getInt("id"), name, verbiouser, userName, fecha, hora, phone, train, country, cuenta, históricoList, saldo);
                ////////////////////////////////////////////////////////////////
                HttpSession userSession = (HttpSession) request.getSession();
                userSession.setAttribute("userActive", user);
                userSession.setMaxInactiveInterval(15 * 60);

                return true;
            } else {
                continue;
            }

        }
        return false;
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
            return decryptedText;
        } catch (Exception ex) {
            return decryptedText;
        }
    }

    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0) {
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                }
                md.update(password);
                if (salt != null) {
                    md.update(salt, 0, 8);
                }
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0) {
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);
            }

            return result;

        } catch (DigestException e) {

            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte) 0);
        }
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

    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
