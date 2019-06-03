package AAADEVRECORDV3.make;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.Encoder;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;

public class VerbioClient {
	private transient final Logger logger = Logger
			.getLogger(VerbioClient.class);

	public void verify(String verbioUser, Call call, String nameuser)
			throws UnsupportedOperationException, IOException,
			SSLUtilityException, NoAttributeFoundException, ServiceNotFoundException {
		final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
		final SSLContext sslContextAssistant = SSLUtilityFactory
				.createSSLContext(protocolTypeAssistant);

		final String URI = "https://avaya:DRNUDUsWh5o3uRdQcZ@cloud2.verbio.com/asv/ws/process";

		final HttpClient clientAssistant = HttpClients.custom()
				.setSslcontext(sslContextAssistant)
				.setHostnameVerifier(new AllowAllHostnameVerifier()).build();
		final HttpPost postMethodAssistant = new HttpPost(URI);
		postMethodAssistant.addHeader("Content-Type", "application/json");
		String base64 = getBase64();
		final String messageBodyAssistant = "{\n" + "	\"user_data\":\n"
				+ "	{\n" + "		\"filename\":\"" + base64 + "\",\n"
				+ "		\"username\": \"" + verbioUser + "\",\n"
				+ "		\"action\": \"VERIFY\",\n" + "		\"score\": \"\",\n"
				+ "		\"spoof\": \"0\",\n" + "		\"grammar\": \"\",\n"
				+ "		\"lang\": \"\"\n" + "	}\n" + "}";
		final StringEntity conversationEntityAssistant = new StringEntity(
				messageBodyAssistant);
		postMethodAssistant.setEntity(conversationEntityAssistant);

		final HttpResponse responseAssistant = clientAssistant
				.execute(postMethodAssistant);

		final BufferedReader inputStreamAssistant = new BufferedReader(
				new InputStreamReader(responseAssistant.getEntity()
						.getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStreamAssistant.readLine()) != null) {
			result.append(line);
		}

		JSONObject json = new JSONObject(result.toString());
		logger.info("VerbioClient response: " + json);

		JSONObject response = json.getJSONObject("response");
		String error = response.getString("error_message");
		String status = response.getString("status");
		if (error != "") {
			logger.info("Error: " + error);
		}
		if (status.equals("SUCCESS")) {
			logger.info("Verbio Response SUCCESS");
			JSONObject resultVerbio = response.getJSONObject("result");
			JSONObject verbioResult = resultVerbio
					.getJSONObject("verbio_result");
			String resultFingerPrint = verbioResult.getString("result");
			String scoreVerbio = verbioResult.getString("score");
			String scoreVoiceRecognition = AttributeStore.INSTANCE.getAttributeValue(Constants.SCORE_VOICE_RECOGNITION);
			// Usuario entrenado
			float scoreNumbre = Float.parseFloat(scoreVerbio);
			float scoreVoiceRecognitionInteger = Float.parseFloat(scoreVoiceRecognition);
			logger.info("scoreNumber");
			logger.info("Verbio scoreNumber: " + scoreNumbre);
			if (scoreNumbre > scoreVoiceRecognitionInteger) {
				// Usuario correcto TTS con el Nombre del Usuario
				logger.info("score < scoreVoiceRecognitionInteger");
				try{
					TTSResponse ttsResponse = new TTSResponse();
					ttsResponse.useridentifiedResponse(call, nameuser);
				}catch(Exception e){
					logger.info("TTSResponse: " + e.toString());
				}
				

			} else {
				logger.info("Usuario no cumple con score minimo de " + scoreVoiceRecognition);
				// Usuario no correcto
				PlayNotUser play = new PlayNotUser(call);
				try {
					play.promptPlayAndExecute();
				} catch (NoAttributeFoundException | ServiceNotFoundException | URISyntaxException | NoUserFoundException | NoServiceProfileFoundException e) {
					logger.info("Error Play Error Verify " + e.toString());
				}

			}

		}

	}

	public String getBase64() {
		String realPath = getApplcatonPath();
		String[] split = realPath.split("/");
		String base64 = null;
		StringBuilder path = new StringBuilder();
		for (int k = 1; k < split.length - 1; k++) {
			path.append("/");
			path.append(split[k]);
		}
		final String filename = RecordingData.INSTANCE.getRecordingFilename();
		if (filename != null) {
			/*
			 * File(String parent, String child) Creates a new File instance
			 * from a parent pathname string and a child pathname string.
			 */
			final File audioFile = new File(path.toString(), filename);
			if (audioFile.exists()) {
				logger.info(audioFile.getAbsoluteFile());
				base64 = Encoder.encoder(audioFile.getAbsolutePath());
			}
		}
		return base64;
	}

	/*****************************************************************************
	 * return application path
	 * 
	 * @return
	 *****************************************************************************/
	public static String getApplcatonPath() {
		CodeSource codeSource = VerbioClient.class.getProtectionDomain()
				.getCodeSource();
		File rootPath = null;
		try {
			rootPath = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return rootPath.getParentFile().getPath();
	}// end of getApplcatonPath()

}
