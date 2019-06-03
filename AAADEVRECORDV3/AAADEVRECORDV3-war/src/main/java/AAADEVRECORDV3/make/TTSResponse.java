package AAADEVRECORDV3.make;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.net.ssl.SSLContext;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

import service.AAADEVRECORDV3.MediaListenerTTSAnnouncement;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

import AAADEVRECORDV3.util.BuscarYRemplazarAcentos;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

public class TTSResponse {
	public static int filesize;
	private final Logger logger = Logger.getLogger(getClass());
	
	public void useridentifiedResponse(Call call, String userName){
		StringBuilder sb = new StringBuilder();
		LanguageAttribute languageAtribute = new LanguageAttribute(call);
		String announcement = null;
		String voice = null;
		if (languageAtribute.getLanguageAttribute().equals("es")) {
		sb.append("Estimado " + userName + "hemos identificado correctamente,"
		+" favor de indicarnos en que podemos ayudar y al terminar presione almuadilla");
		BuscarYRemplazarAcentos español = new BuscarYRemplazarAcentos();
		announcement = español.Español(sb.toString());
		voice = "es-ES_LauraVoice";
		}
		if (languageAtribute.getLanguageAttribute().equals("pt")) {
			sb.append("Caro "+userName+" nós identificamos corretamente,"
		+" por favor, indique em que podemos ajudar e quando terminar pressione almuadilla");
			BuscarYRemplazarAcentos portugues = new BuscarYRemplazarAcentos();
			announcement = portugues.Portugues(sb.toString());
			voice = "pt-BR_IsabelaVoice";
		}
		if (languageAtribute.getLanguageAttribute().equals("en")) {
			sb.append("Dear "+userName+" we have correctly identified"
		+" please indicate in what we can help and when finished press #");
			announcement = sb.toString();
			voice = "en-US_AllisonVoice";
		}
		
		
		try {
			TTSWatsonGenerateAudioFile(announcement, voice, call);
		} catch (SSLUtilityException | IOException e) {
			logger.info("TTSWatsonGenerateAudioFile Error: " + e.toString());
		}
		
	}
	
	public void TTSWatsonGenerateAudioFile(String announcement, String voice, Call call) throws SSLUtilityException, ClientProtocolException, IOException{
		String user = "1a750c00-9343-4032-9e4d-dd485052692d";
		String password = "g7rmue4UsCWP";
		final SSLProtocolType protocolTypeTraductor = SSLProtocolType.TLSv1_2;
		final SSLContext sslContextTraductor = SSLUtilityFactory
				.createSSLContext(protocolTypeTraductor);
		final CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(user, password));
		
		final String URI = "https://stream.watsonplatform.net/text-to-speech/api/v1/synthesize?voice="
				+ voice;
		
		final HttpClient clientTTSpeech = HttpClients.custom()
				.setSslcontext(sslContextTraductor)
				.setHostnameVerifier(new AllowAllHostnameVerifier())
				.build();
		final HttpPost postTTSpeech = new HttpPost(URI);
		postTTSpeech.addHeader("Accept", "audio/l16;rate=8000");
		postTTSpeech.addHeader("Content-Type", "application/json");

		final String authStringTTSpecch = user + ":" + password;
		final String authEncBytesTTSpeech = DatatypeConverter
				.printBase64Binary(authStringTTSpecch.getBytes());
		postTTSpeech.addHeader("Authorization", "Basic "
				+ authEncBytesTTSpeech);

		final String messageBodyTTSpeech = "{\"text\":\""
				+ announcement + "\"}";
		
		final StringEntity conversationEntityTTSpeech = new StringEntity(
				messageBodyTTSpeech);
		postTTSpeech.setEntity(conversationEntityTTSpeech);

		final HttpResponse responseTTSpeech = clientTTSpeech
				.execute(postTTSpeech);

		InputStream in = reWriteWaveHeader(responseTTSpeech.getEntity()
				.getContent());
		
		/*
		 * Determinar el path de almacenamiento
		 */
		String realPath = getApplcatonPath();
		String [] split = realPath.split("/");
		StringBuilder path = new StringBuilder();
	       for(int k = 1 ; k < split.length - 1; k++){
	    	   path.append("/");
	    	   path.append(split[k]);
	       }
	     logger.info("path" + path.toString());
	     
		OutputStream out = new FileOutputStream(path.toString() + "/AAADEVRECORDV3TextToSpeech.wav");
		
		byte[] buffer = new byte[filesize + 8];
		int length;
		while ((length = in.read(buffer)) > 0) {

			InputStream byteAudioStream = new ByteArrayInputStream(
					buffer);
			AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1,
					false, false);
			AudioInputStream audioInputStream = new AudioInputStream(
					byteAudioStream, audioFormat, buffer.length);
			if (AudioSystem.isFileTypeSupported(
					AudioFileFormat.Type.WAVE, audioInputStream)) {
				AudioSystem.write(audioInputStream,
						AudioFileFormat.Type.WAVE, out);
			}

		}

		out.close();
		in.close();
		
		
		try {
			playAnnouncement(call);
		} catch (URISyntaxException e) {
			logger.info("Error playAnnouncement " + e.toString());
		}
	}
	
	public void playAnnouncement(Call call) throws URISyntaxException{
		String interruptibility = "true";
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		LanguageAttribute languageAtribute = new LanguageAttribute(call);
		String announcement = "AAADEVRECORDV3TextToSpeech.wav";
		/*
		 * Determina la URL del servicio
		 */
		final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
		final String trafficInterfaceAddress = addressRetriever
				.getTrafficInterfaceAddress();
		final String myServiceName = ServiceUtil.getServiceDescriptor()
				.getName();
		
		final StringBuilder sb = new StringBuilder();
		sb.append("http://").append(trafficInterfaceAddress)
				.append("/services/").append(myServiceName).append("/")
				.append(announcement);
		/*
		 * PlayWelcome == null reproduce el mensaje de bienvenida
		 * "Bienvenido.wav" es el resultado del stringBuilder
		 */
		PlayItem playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());
		final MediaService mediaService = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		final MediaListenerTTSAnnouncement mediaListenerTTSAnnouncement = new MediaListenerTTSAnnouncement(call);
		mediaService.play(participant, playItem, mediaListenerTTSAnnouncement);
	}
	
	/*
	 * TextToSpeech Methods
	 */
	public static InputStream reWriteWaveHeader(InputStream is)
			throws IOException {
		byte[] audioBytes = toByteArray(is);
		filesize = audioBytes.length - 8;

		writeInt(filesize, audioBytes, 4);
		writeInt(filesize - 8, audioBytes, 74);

		return new ByteArrayInputStream(audioBytes);
	}
	

	private static void writeInt(int value, byte[] array, int offset) {
		for (int i = 0; i < 4; i++) {
			array[offset + i] = (byte) (value >>> (8 * i));
		}
	}

	public static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384]; // 4 kb

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		return buffer.toByteArray();
	}

	/*
	 * End TextToSpeech Methods
	 */
	
	/*****************************************************************************
     * return application path
     * @return
     *****************************************************************************/
    public static String getApplcatonPath(){
        CodeSource codeSource = TTSResponse.class.getProtectionDomain().getCodeSource();
        File rootPath = null;
        try {
            rootPath = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
          return e.toString();
        }           
        return rootPath.getParentFile().getPath();
    }//end of getApplcatonPath()
}
