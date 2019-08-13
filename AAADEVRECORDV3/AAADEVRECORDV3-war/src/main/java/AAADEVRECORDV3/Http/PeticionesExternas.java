package AAADEVRECORDV3.Http;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import service.AAADEVRECORDV3.MyEmailSender;
import AAADEVRECORDV3.Http.Http.IBM.Natural_Language_Understanding;
import AAADEVRECORDV3.Http.Http.IBM.Watson_Assistant;
import AAADEVRECORDV3.Http.PlayAnnouncement.PlayError;
import AAADEVRECORDV3.util.AttributeStore;
import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.Intenciones;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;
import static AAADEVRECORDV3.Http.MakingPost.nombreWav;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.util.logger.Logger;

public class PeticionesExternas {

	private static final Logger logger = Logger
			.getLogger(PeticionesExternas.class);
	private final Call call;

	public PeticionesExternas(final Call call) {
		this.call = call;

	}

	public void peticionesExternas(Participant participant, UUID requestid,
			MediaService mediaServicehelsinky)
			throws NoAttributeFoundException, ServiceNotFoundException,
			URISyntaxException {
		JSONObject json = null;
		String text = null;
		String intent = null;
		String confidence = null;
		/*
		 * Petición a Google Cloud
		 */
		VPSPOST vpsPost = new VPSPOST();
		try {

			String[] arregloGoogle = vpsPost.vpsPOST(call);
			// String [] arregloGoogle = vpsPost.googlePost(call);
			text = arregloGoogle[0];
			confidence = arregloGoogle[1];
			logger.info("Texto : " + text);
			logger.info("Confidence " + confidence);

		} catch (Exception e) {
			mediaServicehelsinky.stop(participant, requestid);
			logger.error("Error vpsPOST : " + e.toString());
			PlayError playError = new PlayError(call);
			playError.audioError();
			new MyEmailSender().sendErrorByEmail("Error vpsPOST : " + e.toString(), call);
		}
		/*
		 * Petición Watson Assistant
		 */
		try {
			logger.info("Petición Watson Assitant");
			Watson_Assistant wapeticion = new Watson_Assistant();
			intent = wapeticion.main(text, call);
		} catch (Exception e) {
			mediaServicehelsinky.stop(participant, requestid);
			PlayError playError = new PlayError(call);
			playError.audioError();
			logger.error("Error Watson_Assistant : " + e.toString());
			new MyEmailSender().sendErrorByEmail("Error Watson_Assistant : " + e.toString(), call);
		}

		/*
		 * Petición a Natural Language Understanding
		 */
		String[] arregloEmociones = null;

		try {
			Natural_Language_Understanding nlupeticion = new Natural_Language_Understanding();
			arregloEmociones = nlupeticion.main(text, call);
			json = new JSONObject();
			json.put("Anger", arregloEmociones[0]);
			json.put("Disgust", arregloEmociones[1]);
			json.put("Fear", arregloEmociones[2]);
			json.put("Joy", arregloEmociones[3]);
			json.put("sadness", arregloEmociones[4]);
			json.put("Intent", intent);
			JSONObject jsonIntent = new JSONObject();
			jsonIntent.put("Intent", intent);
			json.put("Intent", jsonIntent);
			json.put("Transcript", text);
			json.put("COnfidence", confidence);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String fechayHOra = dateFormat.format(date);
			json.put("fechayHora", fechayHOra);
			Participant participant3 = call.getCallingParty();
			json.put("Origen", participant3.getHandle());
			json.put("Destino", AttributeStore.INSTANCE.getAttributeValue(Constants.AGENT_PHONE));

		} catch (NoAttributeFoundException | ServiceNotFoundException e1) {
			mediaServicehelsinky.stop(participant, requestid);
			PlayError playError = new PlayError(call);
			playError.audioError();
			logger.error("Error: Natural_Language_Understanding" + e1.toString());
			new MyEmailSender().sendErrorByEmail("Error: Natural_Language_Understanding" + e1.toString(), call);
		}

		MakingPost postControlPad = new MakingPost(call);
		try {
			postControlPad.makingPOST();
			postControlPad.makingPostIntent(json.toString());
		} catch (Exception e1) {
			logger.error("Error postControlPad : " + e1.toString());
			new MyEmailSender().sendErrorByEmail("Error postControlPad : " + e1.toString(), call);
		}

		/*
		 * Detener musica en epera
		 */
		mediaServicehelsinky.stop(participant, requestid);
		/*
		 * Definir el audio de salida dependiendo del INTENT, regresa la
		 * traducción del audio dependiendo del idioma seleccionado
		 */
		try {
			Intenciones definirIntent = new Intenciones();
			definirIntent.DefinirIntencion(call, intent);
		} catch (Exception e) {
			logger.error("Error Intenciones : " + e);
			new MyEmailSender().sendErrorByEmail("Error Intenciones : " + e, call);
		}

	       
        try {
			final TrafficInterfaceAddressRetrieverImpl addressRetriever = new TrafficInterfaceAddressRetrieverImpl();
			final String trafficInterfaceAddress = addressRetriever.getTrafficInterfaceAddress();
			String emailSubject = "Email AAADEVRECORDV3";
			final MyEmailSender myEmailSender = new MyEmailSender();
			Participant participant2 = call.getCallingParty();
			Date now = new Date(System.currentTimeMillis());
			String horaFecha = (now.toString().replaceAll("[^\\dA-Za-z]", ""));

			StringBuilder emailBody = new StringBuilder();
			emailBody.append("Número de origen: " + participant2.getHandle()
					+ "\n Hora y fecha: " + horaFecha + "\n Transcripción: "
					+ text + "\n Intención: " + intent);
			LanguageAttribute languageAttribute = new LanguageAttribute(call);
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				emailBody
				.append("\n Liga del audio: https://"
						+ trafficInterfaceAddress
						+ "/services/AAADEVCONTROLPAD/ControladorGrabaciones/web/Record/"
						+ nombreWav + ".wav");
			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {
				emailBody
						.append("\n Liga del audio: https://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/ControladorGrabaciones/web/RecordEn/"
								+ nombreWav + ".wav");

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {
				emailBody
						.append("\n Liga del audio: https://"+trafficInterfaceAddress+"/services/AAADEVCONTROLPAD/ControladorGrabaciones/web/RecordPt/"
								+ nombreWav + ".wav");

			}

			myEmailSender.sendEmail(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.EMAIL),
					emailSubject, emailBody.toString());
		} catch (NoAttributeFoundException | ServiceNotFoundException e) {
			logger.error("Error: Enviar Correo" + e.toString());
			new MyEmailSender().sendErrorByEmail("Error: Enviar Correo" + e.toString(), call);
		}

	}

}