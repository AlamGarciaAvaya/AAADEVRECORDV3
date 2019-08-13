package AAADEVRECORDV3.Http.TTS.PlayAnnouncement;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.net.ssl.SSLContext;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVRECORDV3.MyEmailSender;
import AAADEVRECORDV3.Bean.Usuario;
import AAADEVRECORDV3.Http.MediaListeners.MediaListenerTTSAnnouncement;
import AAADEVRECORDV3.Http.PlayAnnouncement.PlayError;
import AAADEVRECORDV3.util.AES;
import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;


public class TTSResponseGoogle {
	private final Call call;
	private final Usuario usuario;
	private transient final Logger logger = Logger
			.getLogger(TTSResponseGoogle.class);
	public TTSResponseGoogle(final Call call, final Usuario usuario){
		this.call = call;
		this.usuario = usuario;
	}
	
	public void useridentifiedResponse() throws NoAttributeFoundException, ServiceNotFoundException, NoUserFoundException, NoServiceProfileFoundException{
		StringBuilder sb = new StringBuilder();
		
		String text = null;
		String voice = null;
		String voiceName = null;
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) {
			sb.append("Estimado " + usuario.getName() + "hemos identificado correctamente,"
			+" favor de indicarnos en que podemos ayudar y al terminar presione almuadilla");
			text = sb.toString();
			voice = "es-ES";
			voiceName = "es-ES-Standard-A";
			}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) {
			sb.append("Caro "+usuario.getName()+" nós identificamos corretamente,"
		+" por favor, indique em que podemos ajudar e quando terminar pressione almuadilla");
			text = sb.toString();
			voice = "pt-BR";
			voiceName = "pt-BR-Standard-A";
		}
		if (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en")) {
			sb.append("Dear "+usuario.getName()+" we have correctly identified"
		+" please indicate in what we can help and when finished press #");
			text = sb.toString();
			voice = "en-US";
			voiceName = "en-US-Wavenet-C";
		}
		
		try{
//			String responseGoogle = googleRequestTTS(text, voice, voiceName);
			String responseGoogle = makeVPSRequest(text, voice, voiceName);
			if(responseGoogle == null || responseGoogle.isEmpty()){
				throw new Exception("responseGoogle es Igual a Null");
			}else{
				makeAudioFile(responseGoogle);
			}
			
			playTTSResponse();

		}catch(Exception e){
			PlayError play = new PlayError(call);
			play.audioError();
			logger.error("Error TTSResponse: " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error TTSResponse: " + e.toString(), call);
		}
		
	}
	
	private void playTTSResponse() throws URISyntaxException{
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
				.append("GoogleCloudTTS.wav");
		PlayItem playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());
		final MediaService mediaService = MediaFactory.createMediaService();
		final MediaListenerTTSAnnouncement mediaListenerTTSAnnouncement = new MediaListenerTTSAnnouncement(call);
		mediaService.play(call.getCallingParty(), playItem, mediaListenerTTSAnnouncement);
	}
	
	
	public String googleRequestTTS(String text, String voice, String voiceName)
			throws SSLUtilityException, ClientProtocolException, IOException,
			NoAttributeFoundException, ServiceNotFoundException, NoUserFoundException, NoServiceProfileFoundException {

		final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
		final SSLContext sslContextAssistant = SSLUtilityFactory
				.createSSLContext(protocolTypeAssistant);
		

		String apiKey = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.GOOGLE_CLOUD_API_KEY);
		final String URI = "https://texttospeech.googleapis.com/v1/text:synthesize?key="
				+ apiKey;

		final HttpClient client = HttpClients.custom()
				.setSSLContext(sslContextAssistant)
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		final HttpPost postMethod = new HttpPost(URI);
		postMethod.addHeader("Content-Type", "application/json");

		final StringEntity ttsEntity = new StringEntity(
				createJsonPayLoadRequest(text, voice, voiceName));
		postMethod.setEntity(ttsEntity);
		final HttpResponse response = client.execute(postMethod);

		final BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStream.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
	
    public String makeVPSRequest(String text, String voice,String voiceName) throws ClientProtocolException, IOException,
			NoAttributeFoundException, ServiceNotFoundException {
    	String apiKey = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.GOOGLE_CLOUD_API_KEY);
		final HttpClient client = HttpClients.createDefault();
		String vpsPostFQDN = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.VPS_FQDN);
		HttpPost postMethod = new HttpPost("http://" + vpsPostFQDN
				+ "/AAADEVURIEL_PRUEBAS_WATSON-war-1.0.0.0.0/TTS");
		AES aes = new AES();
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
		StringBody apiKeyBody = new StringBody(aes.encrypt(apiKey), ContentType.TEXT_PLAIN);
		StringBody textBody = new StringBody(aes.encrypt(text), ContentType.TEXT_PLAIN);
		StringBody voiceBody = new StringBody(aes.encrypt(voice), ContentType.TEXT_PLAIN);
		StringBody voiceNameBody = new StringBody(aes.encrypt(voiceName), ContentType.TEXT_PLAIN);

    	reqEntity.addPart("apiKey", apiKeyBody);
    	reqEntity.addPart("text", textBody);
    	reqEntity.addPart("voice", voiceBody);
    	reqEntity.addPart("voiceName", voiceNameBody);
    	HttpEntity entity = reqEntity.build();
    	
    	postMethod.setEntity(entity);

		final HttpResponse response = client.execute(postMethod);

		final BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStream.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
	
	
	private void makeAudioFile(String responseGoogle) throws MessagingException, IOException{
		JSONObject json = new JSONObject(responseGoogle);
		 if (json.has("audioContent")) {
			 String realPath = getApplcatonPath();
       		String [] split = realPath.split("/");
       		StringBuilder path = new StringBuilder();
       	       for(int k = 1 ; k < split.length - 1; k++){
       	    	   path.append("/");
       	    	   path.append(split[k]);
       	       }
               String base64String = json.getString("audioContent");
               final FileOutputStream saveAudioFile = new FileOutputStream(path.toString() + "/GoogleCloudTTS.wav");
               InputStream audioInput = new ByteArrayInputStream(base64String.getBytes());
               final byte audioBytes[] = base64String.getBytes("UTF-8");

               while ((audioInput.read(audioBytes)) != -1) {
                   InputStream byteAudioStream = new ByteArrayInputStream(decode(audioBytes));
                   final AudioFormat audioFormat = getAudioFormat();
                   AudioInputStream audioInputStream = new AudioInputStream(byteAudioStream, audioFormat, audioBytes.length);

                   if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE,
                           audioInputStream)) {
                       AudioSystem.write(audioInputStream,
                               AudioFileFormat.Type.WAVE, saveAudioFile);
                   }

               }
               audioInput.close();
               saveAudioFile.flush();
               saveAudioFile.close();
		 }else{
			 logger.error(json);
			 throw new NullPointerException("Error NO existe audioContent");
		 }
	}
	
	public static String getApplcatonPath(){
        CodeSource codeSource = TTSResponseGoogle.class.getProtectionDomain().getCodeSource();
        File rootPath = null;
        try {
            rootPath = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
          return e.toString();
        }           
        return rootPath.getParentFile().getPath();
    }//end of getApplcatonPath()


	public static byte[] decode(byte[] encodedAudioBytes)
			throws MessagingException, IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				encodedAudioBytes);
		InputStream b64InputStream = MimeUtility.decode(byteArrayInputStream,
				"base64");

		byte[] tmpAudioBytes = new byte[encodedAudioBytes.length];
		int numberOfBytes = b64InputStream.read(tmpAudioBytes);
		byte[] decodedAudioBytes = new byte[numberOfBytes];

		System.arraycopy(tmpAudioBytes, 0, decodedAudioBytes, 0, numberOfBytes);

		return decodedAudioBytes;
	}
	
    /*
	 * Avaya recommends that audio played by Avaya Aura MS be encoded as 16-bit,
	 * 8 kHz, single channel, PCM files. Codecs other than PCM or using higher
	 * sampling rates for higher quality recordings can also be used, however,
	 * with reduced system performance. Multiple channels, like stereo, are not
	 * supported.
	 * 
	 * @see Using Web Services on Avaya Aura Media Server Release 7.7, Issue 1,
	 * August 2015 on support.avaya.com
     */
    private static AudioFormat getAudioFormat() {
        final float sampleRate = 8000.0F;
        // 8000,11025,16000,22050,44100
        final int sampleSizeInBits = 16;
        // 8,16
        final int channels = 1;
        // 1,2
        final boolean signed = true;
        // true,false
        final boolean bigEndian = false;
        // true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
                bigEndian);
    }
    
    private String createJsonPayLoadRequest(String text, String voice,
			String voiceName) {
		JSONObject json = new JSONObject();

		JSONObject jsonAudioConfig = new JSONObject()
				.put("audioEncoding", "LINEAR16")
				.put("sampleRateHertz", 8000)
				.put("effectsProfileId",
						new JSONArray().put("telephony-class-application"));
		JSONObject jsonInput = new JSONObject().put("text", text);
		JSONObject jsonVoice = new JSONObject().put("languageCode", voice).put(
				"name", voiceName);

		json.put("audioConfig", jsonAudioConfig);
		json.put("input", jsonInput);
		json.put("voice", jsonVoice);

		return json.toString();
	}
}
