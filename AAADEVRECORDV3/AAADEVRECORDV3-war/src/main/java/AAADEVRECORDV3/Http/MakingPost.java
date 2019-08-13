package AAADEVRECORDV3.Http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;

import service.AAADEVRECORDV3.MyEmailSender;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

public class MakingPost {
	private final Call call;
	private transient final Logger logger = Logger
			.getLogger(MakingPost.class);
	public static String nombreWav = null;
	
	public MakingPost(final Call call){
		this.call = call;
	}

	public void makingPOST() throws IOException, NoAttributeFoundException, ServiceNotFoundException, NoUserFoundException, NoServiceProfileFoundException {
		try {
			final HttpClient client = HttpClients.createDefault();
			
			final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
			final String trafficInterfaceAddress = addressRetriever
					.getTrafficInterfaceAddress();
			final String myServiceName = ServiceUtil.getServiceDescriptor()
					.getName();

			
			HttpPost httpPost = new HttpPost("http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/ControladorGrabaciones/");

			/*
			 * extensión a la que se llama
			 */
			Participant participant1 = call.getCalledParty();
			String origen = participant1.getHandle();
			/*
			 * Extensión que llama
			 */
			Participant participant2 = call.getCallingParty();
			String destino = participant2.getHandle();
			
			/*
			 * Obtener Fecha
			 */
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date(System.currentTimeMillis());
			String fecha = dateFormat.format(date);
			fecha = fecha.replaceAll("[^\\dA-Za-z]", "");
			
			
			/*
			 * Obtener hora
			 */
			DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ssz");
			Date hora = new Date(System.currentTimeMillis());
			String tiempo = dateFormat2.format(hora);
			tiempo = tiempo.replaceAll("[^\\dA-Za-z]", "");
			
			
			nombreWav = fecha + "_" + tiempo + "_" + origen + "_" + destino ;
			
			logger.info(nombreWav);
			
			/*
			 * Determinar el path de almacenamiento
			 */
			String realPath = getApplcatonPath();
			String[] split = realPath.split("/");
			StringBuilder path = new StringBuilder();
			for (int k = 1; k < split.length - 1; k++) {
				path.append("/");
				path.append(split[k]);
			}

			String nombreWavfile = nombreWav + ".wav";
			FileBody bin = new FileBody(new File(path.toString()+"/recording"+ myServiceName + ".wav"));
			StringBody comment = new StringBody(nombreWavfile, ContentType.TEXT_PLAIN);
			
			LanguageAttribute languageAttribute = new LanguageAttribute(call);
			StringBody comment2 = null;
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				comment2 = new StringBody("http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/Record/", ContentType.TEXT_PLAIN);
			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				comment2 = new StringBody("http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/RecordEn/", ContentType.TEXT_PLAIN);
			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				comment2 = new StringBody("http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/RecordPt/", ContentType.TEXT_PLAIN);
			}

			MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
			reqEntity.addPart("rec_data", bin);
			reqEntity.addPart("recFileName", comment);
			reqEntity.addPart("restRecordURI", comment2);
			
			HttpEntity entity = reqEntity.build();
			httpPost.setEntity(entity);
			
			HttpResponse response = client.execute(httpPost);

			final BufferedReader inputStream = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.ISO_8859_1));

			String line = "";
			final StringBuilder result = new StringBuilder();
			while ((line = inputStream.readLine()) != null) {
				result.append(line);
			}
			logger.info("makingPOST Response: " + result.toString());
		} catch (UnsupportedEncodingException ex) {
			logger.error("Error makingPOST: " + ex);
			new MyEmailSender().sendErrorByEmail("Error makingPOST: " + ex.toString(), call);
		}
	}

	
	public void makingPostIntent(String json) throws IOException, NoAttributeFoundException, ServiceNotFoundException, NoUserFoundException, NoServiceProfileFoundException {
		try {
			final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
			final String trafficInterfaceAddress = addressRetriever
					.getTrafficInterfaceAddress();
		
			final HttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/inputIntent/");;

			String nombreIntenet = nombreWav + ".txt";
			StringBody bin = new StringBody(json, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
			StringBody comment = new StringBody(nombreIntenet, ContentType.TEXT_PLAIN);
			StringBody comment2 = new StringBody("http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/Intent/", ContentType.TEXT_PLAIN);

			MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
			reqEntity.addPart("rec_data", bin);
			reqEntity.addPart("recFileName", comment);
			reqEntity.addPart("restRecordURI", comment2);
			
			HttpEntity entity = reqEntity.build();
			httpPost.setEntity(entity);
	
			HttpResponse response = client.execute(httpPost);

			final BufferedReader inputStream = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.ISO_8859_1));

			String line = "";
			final StringBuilder result = new StringBuilder();
			while ((line = inputStream.readLine()) != null) {
				result.append(line);
			}
			logger.info("makingPostIntent Response: " + result.toString());

		} catch (UnsupportedEncodingException ex) {
			logger.error("Error makingPostIntent: " + ex);
			new MyEmailSender().sendErrorByEmail("Error makingPostIntent: " + ex.toString(), call);
			
		}
	}
	
	/*****************************************************************************
	 * return application path
	 * 
	 * @return
	 *****************************************************************************/
	public static String getApplcatonPath() {
		CodeSource codeSource = MakingPost.class.getProtectionDomain()
				.getCodeSource();
		File rootPath = null;
		try {
			rootPath = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootPath.getParentFile().getPath();
	}// end of getApplcatonPath()
	
}