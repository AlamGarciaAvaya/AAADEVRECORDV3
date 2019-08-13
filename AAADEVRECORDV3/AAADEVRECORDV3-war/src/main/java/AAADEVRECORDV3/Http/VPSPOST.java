package AAADEVRECORDV3.Http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.LanguageAttribute;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.util.logger.Logger;

public class VPSPOST {

	private transient final Logger logger = Logger.getLogger(getClass());

	public String[] vpsPOST(Call call) throws Exception {
		String idioma = null;
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		if (languageAttribute.getLanguageAttribute().equals("es")) {
			logger.info("Se definio el idioma Español");
			idioma = "es-MX";

		}
		if (languageAttribute.getLanguageAttribute().equals("en")) {
			logger.info("Se definio el idioma Ingles");
			idioma = "en-US";
		}
		if (languageAttribute.getLanguageAttribute().equals("pt")) {
			logger.info("Se definio el idioma Portugues");
			idioma = "pt-BR";

		}

		String[] exitCodes = { null, null };
		
		String vpsPostFQDN = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.VPS_FQDN);
		
		final String URI = "http://"+vpsPostFQDN+"/AAADEVURIEL_PRUEBAS_WATSON-war-1.0.0.0.0/TranscriptAAADEVRECORDV3?apikey="
				+ AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.GOOGLE_CLOUD_API_KEY)
				+ "&idioma=" + idioma + "&audio=recordingAAADEVRECORDV3.wav";

		final HttpClient clientSpeech = HttpClients.createDefault();

		final HttpPost postMethodSpeech = new HttpPost(URI);

		postMethodSpeech.addHeader("Accept", "application/json; charset=UTF-8");
		postMethodSpeech.addHeader("Content-Type",
				"application/json; charset=UTF-8");

		final String messageBodySpeech = "";
		final StringEntity conversationEntitySpeech = new StringEntity(
				messageBodySpeech);
		postMethodSpeech.setEntity(conversationEntitySpeech);

		final HttpResponse responseSpeech = clientSpeech
				.execute(postMethodSpeech);

		final BufferedReader inputStreamSpeech = new BufferedReader(
				new InputStreamReader(responseSpeech.getEntity().getContent(),
						StandardCharsets.ISO_8859_1));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStreamSpeech.readLine()) != null) {
			result.append(line);
		}

		JSONObject json = new JSONObject(result.toString());

		String transcript = json.getString("results");
		JSONArray array = new JSONArray(transcript);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			// exitCode = object.get("alternatives").toString();
			String alternatives = object.getString("alternatives");
			JSONArray array2 = new JSONArray(alternatives);
			for (int j = 0; j < array2.length(); j++) {
				JSONObject object2 = array2.getJSONObject(i);
				exitCodes[0] = object2.get("transcript").toString();
				exitCodes[1] = object2.get("confidence").toString();
			}

		}

		inputStreamSpeech.close();
		postMethodSpeech.reset();

		return exitCodes;

	}


}