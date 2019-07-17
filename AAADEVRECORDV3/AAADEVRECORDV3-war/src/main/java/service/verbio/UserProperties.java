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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

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
import static service.verbio.LogIn.decryptText;
import static service.verbio.LogIn.readFile;
import service.verbio.bean.Usuario;

/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "UserProperties", urlPatterns = {"/UserProperties"})
public class UserProperties extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(getClass());
	
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

        } else {
            if (actionString.equals("userProp")) {
                json.put("user", user.getUsername());
                json.put("real_name", user.getName());
                json.put("verbio_user", user.getVerbiouser());
                json.put("date", user.getFecha());
                json.put("hour", user.getHora());
                json.put("phone_active", user.getPhone());
                json.put("verbio_train", user.getTrain());

                json.put("country", user.getCountry());

                //MODIFICACIÓN 10 DE JULIO
                if (!user.getCuenta().equals("")) {
                    json.put("saldoCajaSocial", user.getSaldo());
                    json.put("cuentaCajaSocial", user.getCuenta());
                    JSONArray jsonArrayHistoricos = new JSONArray();
                    for (int i = 0; i < user.getHistóricoList().size(); i++) {
                        jsonArrayHistoricos.put(user.getHistóricoList().get(i));
                    }
                    json.put("historicoMovimientos", jsonArrayHistoricos);
                }
                /////////////////////////////
                if (user.getUsername().equals("umansilla@avaya.com") || user.getUsername().equals("jlramirez@breeze.com")) {
                    json.put("admin", "admin");
                }
            }
            if (actionString.equals("closeSession")) {
                session.invalidate();
                json.put("status", "ok");

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
                    json.put("status", "updated");
                } catch (Exception e) {
                    logger.error("Create Verbio Error " + e.toString());
                    json.put("error", "error");
                }

            }

            if (actionString.equals("saveSettings")) {
                String nameString = getStringValue(request.getPart("name"));
                String phoneString = getStringValue(request.getPart("phone"));
                String countryString = getStringValue(request.getPart("country"));
                String passwordString = getStringValue(request.getPart("password"));
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
                    json.put("status", "updated");
                } catch (IOException | JSONException e) {
                    logger.error("Save Settings Error " + e.toString());
                    json.put("error", "error");
                }

            }
            //CREADO EL 10 DE JULIO 2019
            if (actionString.equals("createCajaNacional")) {
                try {
                    JSONObject jsonReturn = new JSONObject();
                    jsonReturn = createCajaNacional(user);
                    user.setCuenta(jsonReturn.getString("Cuenta_Caja_Social"));
                    user.setSaldo(jsonReturn.getString("Saldo_Caja_Social"));
                    JSONArray jsonArray = jsonReturn.getJSONArray("Historico_Caja_Social");
                    ArrayList<String> historicoList = new ArrayList<>();
                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        historicoList.add(jsonArray.getString(i));
                    }
                    user.setHistóricoList(historicoList);
                    session.setAttribute("userActive", user);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("Create Caja Social Error: " + e.toString());
                    json.put("error", "error");
                }
            }
            if (actionString.equals("refreshAccount")) {
                try {
                    String newAccount = refreshAccount(user);
                    user.setCuenta(newAccount);
                    session.setAttribute("userActive", user);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("Refresh Account Error " + e.toString());
                    json.put("error", "error");
                }

            }
            if (actionString.equals("newBalance")) {
                try {
                    newBalance(user, getStringValue(request.getPart("balance")));
                    user.setSaldo(getStringValue(request.getPart("balance")));
                    session.setAttribute("userActive", user);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("New Balance Error " + e.toString());
                    json.put("error", "error");
                }
            }
            if (actionString.equals("addMovement")) {
                try {
                    String movement = addNewMovement(user, getStringValue(request.getPart("movement")));
                    user.getHistóricoList().add(movement);
                    session.setAttribute("userActive", user);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("Add Movement Error " + e.toString());
                    json.put("error", "error");
                }

            }
            if (actionString.equals("deleteMovement")) {
                try {
                    Part movement = request.getPart("movement");
                    String movementString = getStringValue(movement);
                    logger.info(movementString);
                    JSONArray jsonNewMovements = removeMovement(user, movementString);
                    user.getHistóricoList().clear();
                    for (int i = 0; i <= jsonNewMovements.length() - 1; i++) {
                        user.getHistóricoList().add(jsonNewMovements.getString(i));
                    }
                    session.setAttribute("userActive", user);
                    json.put("status", "ok");
                } catch (Exception e) {
                    logger.error("Delete Movement Error: " + e.toString());
                    json.put("error", "error");
                }
            }

            /////////////////////////////
        }
        out.println(json);
    }

    public JSONArray removeMovement(Usuario user, String movementString) throws IOException {
        JSONArray nuevoJsonArray = new JSONArray();
        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                JSONArray jsonArrayHistorico = jsonAccount.getJSONArray("Historico_Caja_Social");

                for (int j = 0; j <= jsonArrayHistorico.length() - 1; j++) {
                	logger.info(jsonArrayHistorico.get(j));
                    if (!movementString.equals(jsonArrayHistorico.get(j))) {
                        nuevoJsonArray.put(jsonArrayHistorico.get(j));
                    }
                }

                jobj.getJSONObject(i).getJSONObject("Caja_Social").put("Historico_Caja_Social", nuevoJsonArray);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter("home/wsuser/web/LogIn/Access.txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return nuevoJsonArray;
    }

    public String addNewMovement(Usuario user, String movementString) throws IOException {
        String historico = null;
        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                JSONArray jsonArrayHistorico = jsonAccount.getJSONArray("Historico_Caja_Social");
                
                String strDateFormat = "hh:mm:ss a dd MMMM yyyy";
                SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
                
                Date historicoOne = new Date();
                historico = objSDF.format(historicoOne) + " " + movementString;

                jsonArrayHistorico.put(historico);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter("home/wsuser/web/LogIn/Access.txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return historico;
    }

    public void newBalance(Usuario user, String balanceString) throws IOException {
        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                jsonAccount.put("Saldo_Caja_Social", balanceString);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter("home/wsuser/web/LogIn/Access.txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();

    }

    public String refreshAccount(Usuario user) throws IOException {
        String newAccount = generateRandomNumber(6);
        logger.info("newAccount " + newAccount);
        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i).getJSONObject("Caja_Social");
                jsonAccount.put("Cuenta_Caja_Social", newAccount);
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter("home/wsuser/web/LogIn/Access.txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return newAccount;
    }

    //CREADO EL 10 DE JULIO 2019
    public JSONObject createCajaNacional(Usuario user) throws IOException, Exception {
        JSONObject jsonProperties = new JSONObject();
        JSONArray jsonArrayHistoricos = new JSONArray();
        String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                if (jobj.getJSONObject(i).has("Caja_Social")) {
                    throw new Exception("El usuario ya cuenta con sessión");
                } else {
                    jsonProperties.put("Cuenta_Caja_Social", generateRandomNumber(6));
                    jsonProperties.put("Saldo_Caja_Social", "0.00");
                    String strDateFormat = "hh:mm:ss a dd MMMM yyyy";
                    SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
                    
                    Date historicoOne = new Date();
                    String primerHistorico = objSDF.format(historicoOne) + " Trial session has been created, Historical first";

                    jsonArrayHistoricos.put(primerHistorico);
                    
                    Date historicoTwo = new Date();
                    String SegundoHistorico = objSDF.format(historicoTwo) + " Trial session has been created, Historical Second";

                    jsonArrayHistoricos.put(SegundoHistorico);

                    jsonProperties.put("Historico_Caja_Social", jsonArrayHistoricos);
                    jobj.getJSONObject(i).put("Caja_Social", jsonProperties);
                }
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter("home/wsuser/web/LogIn/Access.txt");
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();
        return jsonProperties;
    }

    public String generateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }

    ///////////////////////////////////////////////////////////////////////////////////////
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
            return decryptedText;
        } catch (Exception ex) {
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
