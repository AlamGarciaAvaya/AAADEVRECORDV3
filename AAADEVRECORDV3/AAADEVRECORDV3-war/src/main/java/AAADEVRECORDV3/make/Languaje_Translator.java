package AAADEVRECORDV3.make;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;


/**
 *
 * @author umansilla
 */
@SuppressWarnings("deprecation")
public class Languaje_Translator {

	/**
	 * @param texto
	 * @return
	 * Ingresa el texto que desea transcribir, Regresa el texto traducido, de acuerdo al Model-id proporcionado
	 * myBeanObj_LT es una variable statica que se inicializa de acuerdo al idioma que se requere, desde el Front-end
	 * https://console.bluemix.net/docs/services/language-translator/translation-models.html#translation-models
	 * @throws ServiceNotFoundException 
	 * @throws NoAttributeFoundException 
	 */
	public static String main(String texto, String modelId) throws NoAttributeFoundException, ServiceNotFoundException {
		/*
		 * HTTPS
		 */
		String exitCode = null;
		String user = "apikey";
		String password = AttributeStore.INSTANCE.getAttributeValue(Constants.LT_API_KEY);
		try {
		      final SSLProtocolType protocolTypeTraductor = SSLProtocolType.TLSv1_2;
		      final SSLContext sslContextTraductor = SSLUtilityFactory.createSSLContext(protocolTypeTraductor);
			final CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(user, password));

			final String URI = "https://gateway.watsonplatform.net/language-translator/api/v3/translate?version=2018-05-01";

			final HttpClient clientTraductor = HttpClients.custom().setSslcontext(sslContextTraductor).setHostnameVerifier(new AllowAllHostnameVerifier()).build();
			final HttpPost postMethodTraductor = new HttpPost(URI);
			postMethodTraductor.addHeader("Accept", "application/json");
			postMethodTraductor.addHeader("Content-Type", "application/json");

			final String authStringTraductor = user + ":" + password;
			final String authEncBytesTraductor = DatatypeConverter
					.printBase64Binary(authStringTraductor.getBytes());
			postMethodTraductor.addHeader("Authorization", "Basic " + authEncBytesTraductor);

			final String messageBodyTraductor = "{\"text\":[\""+texto+"\"],\"model_id\":\""+modelId+"\"}";
			final StringEntity conversationEntityTraductor = new StringEntity(messageBodyTraductor);
			postMethodTraductor.setEntity(conversationEntityTraductor);

			final HttpResponse responseTraductor = clientTraductor.execute(postMethodTraductor);

			final BufferedReader inputStreamTraductor = new BufferedReader(
					new InputStreamReader(responseTraductor.getEntity().getContent()));

			String line = "";
			final StringBuilder result = new StringBuilder();
			while ((line = inputStreamTraductor.readLine()) != null) {
				result.append(line);
			}

			JSONObject json = new JSONObject(result.toString());
			
			String translate = json.getString("translations");
			JSONArray array = new JSONArray(translate);
			for (int i = 0; i < array.length(); i++) {
			    JSONObject object = array.getJSONObject(i);
			    exitCode = object.get("translation").toString();
			}
			
			inputStreamTraductor.close();
			postMethodTraductor.reset();
		} catch (Exception ex) {
			String Error = "Error " + ex;
			return Error;
		}

		return exitCode;

	}
}