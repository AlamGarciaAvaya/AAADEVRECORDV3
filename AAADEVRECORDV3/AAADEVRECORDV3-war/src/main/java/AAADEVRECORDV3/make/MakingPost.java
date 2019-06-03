package AAADEVRECORDV3.make;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

@SuppressWarnings({ "unused", "deprecation" })
public class MakingPost {
	private final Call call;
	private transient final Logger logger = Logger
			.getLogger(MakingPost.class);
	public static String nombreWav = null;
	
	public MakingPost(final Call call){
		this.call = call;
	}

	@SuppressWarnings({ "resource" })
	public void makingPOST() throws IOException {
		try {
			logger.info("makingPOST()");

			DefaultHttpClient httpclient = new DefaultHttpClient();
			
			final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
			final String trafficInterfaceAddress = addressRetriever
					.getTrafficInterfaceAddress();
			final String myServiceName = ServiceUtil.getServiceDescriptor()
					.getName();

			
			HttpPost httppost = new HttpPost(
					"http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/ControladorGrabaciones/");

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
			logger.info("path " + path.toString());

			
			String nombreWavfile = nombreWav + ".wav";
			FileBody bin = new FileBody(new File(path.toString()+"/recording"+ myServiceName + ".wav"));
			StringBody comment = new StringBody(nombreWavfile);
			
			LanguageAttribute languageAttribute = new LanguageAttribute(call);
			StringBody comment2 = null;
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				comment2 = new StringBody(
						"http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/Record/");
			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				comment2 = new StringBody(
						"http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/RecordEn/");
			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				comment2 = new StringBody(
						"http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/RecordPt/");
			}

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("rec_data", bin);
			reqEntity.addPart("recFileName", comment);
			reqEntity.addPart("restRecordURI", comment2);
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

		} catch (UnsupportedEncodingException ex) {
			logger.error("Error: " + ex);
		}
	}

	
	@SuppressWarnings({ "resource" })
	public void makingPostIntent(String json) throws IOException {
		try {
			logger.info("makingPostIntent()");
			final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
			final String trafficInterfaceAddress = addressRetriever
					.getTrafficInterfaceAddress();
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/inputIntent/");;

			String nombreIntenet = nombreWav + ".txt";
			logger.info(nombreWav);
			StringBody bin = new StringBody(json, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
			StringBody comment = new StringBody(nombreIntenet);
			StringBody comment2 = new StringBody(
					"http://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/web/Intent/");

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("rec_data", bin);
			reqEntity.addPart("recFileName", comment);
			reqEntity.addPart("restRecordURI", comment2);
			
			httppost.setEntity(reqEntity);
	
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

		} catch (UnsupportedEncodingException ex) {
			logger.error("Error: " + ex);
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