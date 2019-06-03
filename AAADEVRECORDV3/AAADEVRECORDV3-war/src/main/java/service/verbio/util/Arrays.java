/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.verbio.util;

/**
 *
 * @author umansilla
 */
import java.io.File;

import org.json.JSONObject;

/**
 *
 * @author umansilla
 */
public class Arrays {

    /**
     * @return En lista en formato Json los audios que se recuperan, de acuerdo
     * a la direccion proporcionada en el método GET a Controlador de
     * Grabaciones.
     */
    public static JSONObject main(String storeLocation) {
        JSONObject json = new JSONObject();
        int index = 0;
        String dirPath = storeLocation;
        File dir = new File(dirPath);
        String[] files = dir.list();
        if (files.length == 0) {
            json.put("status", "empty");
        } else {
            for (int contador = files.length - 1; contador >= 0; contador--) {
                json.put("Index" + index, files[contador]);
                index++;
            }
        }

        return json;
    }

}
