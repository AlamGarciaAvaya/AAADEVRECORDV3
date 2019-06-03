package service.AAADEVRECORDV3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URISyntaxException;

import org.json.JSONArray;

import AAADEVRECORDV3.util.Constants;
import AAADEVRECORDV3.util.LanguageAttribute;
import AAADEVRECORDV3.util.TrafficInterfaceAddressRetrieverImpl;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.NoServiceProfileFoundException;
import com.avaya.collaboration.businessdata.api.NoUserFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.CallListenerAbstract;
import com.avaya.collaboration.call.CallTerminationCause;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.TheCallListener;
import com.avaya.collaboration.call.media.MediaFactory;
import com.avaya.collaboration.call.media.MediaServerInclusion;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.call.media.PlayItem;
import com.avaya.collaboration.util.logger.Logger;
import com.avaya.zephyr.platform.dal.api.ServiceUtil;

/*
 * This class is needed if an application with call features is written.
 * If you have an application which is doing only HTTP related operations, remove this class from the project.
 * 
 * For HTTP only application, also remove the sip.xml from src/main/java/webapp/WEB-INF and blank out details from
 * CARRule.xml. Look at the files for more details.
 * 
 */
@TheCallListener
public class MyCallListener extends CallListenerAbstract {
	private final Logger logger;

	public MyCallListener() {

		logger = Logger.getLogger(MyCallListener.class);

	}

	@Override
	public final void callIntercepted(final Call call) {
		logger.fine("Entered callIntercepted.");
		
		if (call.isCalledPhase()) {
			try{
			
			String jsonData = readFile("home/wsuser/web/LogIn/Access.txt");
			JSONArray jobj = new JSONArray(jsonData);
			for (int i = 0; i < jobj.length(); i++) {
				String phone = jobj.getJSONObject(i).has("phone") ? jobj
						.getJSONObject(i).getString("phone") : "null";
						Participant callingParticipant = call.getCallingParty();
				if (phone.equals(callingParticipant.getHandle())) {
					logger.info("Calling number = " + callingParticipant.getHandle());
					String train = jobj.getJSONObject(i).getString("train");
					{
						if (train.equals("yes")) {
							try {
								logger.info("Ususario entrenado");
								String verbiouser = jobj.getJSONObject(i)
										.getString("verbiouser");
								String nameuser = jobj.getJSONObject(i)
										.getString("name");
								playWelcomeVerify(call, verbiouser, nameuser);
								break;
							} catch (URISyntaxException e) {
								logger.info("URISyntaxExceptions "
										+ e.toString());
							}
						} else {
							try {
								logger.info("Ususario No Entrenado");
								promptPlayAndExecute(call);
								break;
							} catch (URISyntaxException
									| NoAttributeFoundException
									| ServiceNotFoundException
									| NoUserFoundException
									| NoServiceProfileFoundException e) {
								logger.info("promptPlayAndExcecuteError "
										+ e.toString());
							}
						}
					}
				}
				
				if (i == (jobj.length() - 1)) {
					// No encontró Coincidencias
					logger.info("No encontró registro del número llamado");
					try {
						promptPlayAndExecute(call);
						break;
					} catch (URISyntaxException | NoAttributeFoundException
							| ServiceNotFoundException | NoUserFoundException
							| NoServiceProfileFoundException e) {
						logger.info("promptPlayAndExcecuteError "
								+ e.toString());
					}

				}
			}
			
			
			
			}catch(Exception e){
				logger.info("Error CallListener "+e.toString());
			}

		} else {
			logger.info("Snap-in sequenced in calling phase");
		}
	}

	@Override
	public void callAlerting(final Participant participant) {
	}

	@Override
	public void participantDropped(final Call call,
			final Participant droppedParticipant,
			final CallTerminationCause cause) {

	}

	@Override
	public void callAnswered(final Call call) {
		call.allow();
	}

	@Override
	public void callTerminated(final Call call, final CallTerminationCause cause) {
		/*
		 * public final class CallTerminationCause extends Enum An enumeration
		 * of the Call Termination Cause which can be used to find reason of
		 * call termination.
		 */
		if (cause == CallTerminationCause.AFTER_ANSWER) {
			/*
			 * AFTER_ANSWER Call ended normally after it was answered.
			 */
		} else if (cause == CallTerminationCause.ABANDONED
				|| cause == CallTerminationCause.REJECTED
				|| cause == CallTerminationCause.TARGET_DID_NOT_RESPOND) {
			/*
			 * ABANDONED Calling party abandoned call before it was answered.
			 * 
			 * REJECTED The called party rejected the call
			 * 
			 * TARGET_DID_NOT_RESPOND Called target did not respond.
			 */

		}

	}

	public static String readFile(String filename) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void playWelcomeVerify(final Call call, final String verbioUser,
			String nameuser) throws URISyntaxException {
		// set the Media Inclusion Policy to AS_NEEDED. Media Server is not
		// required for the whole duration of the call.
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		LanguageAttribute languageAtribute = new LanguageAttribute(call);
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		if (languageAtribute.getLanguageAttribute().equals("es")) {

			// Bienvenido al Sistema de Atencion con Lenguage Natural. Para ser
			// identificado,
			// favor de repetir la frase "En avaya mi voz es mi contraseña"
			// después del tono.
			announcement = "BienvenidoVerificar_es.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("en")) {
			announcement = "WelcomeVerify_en.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("pt")) {
			announcement = "Bem_vindoVerificar_pt.wav";
		}
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

		PlayItem playItem = null;

		/*
		 * PlayWelcome == null reproduce el mensaje de bienvenida
		 * "Bienvenido.wav" es el resultado del stringBuilder
		 */
		playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		final MediaListenerVerify mediaListenerVerify = new MediaListenerVerify(
				call, verbioUser, nameuser);
		mediaService.play(participant, playItem, mediaListenerVerify);

	}

	private void promptPlayAndExecute(final Call call)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException, NoUserFoundException,
			NoServiceProfileFoundException {
		// set the Media Inclusion Policy to AS_NEEDED. Media Server is not
		// required for the whole duration of the call.
		call.getCallPolicies().setMediaServerInclusion(
				MediaServerInclusion.AS_NEEDED);
		LanguageAttribute languageAtribute = new LanguageAttribute(call);
		String announcement = null;
		/*
		 * Solicitar el idioma por Service Profile
		 */
		if (languageAtribute.getLanguageAttribute().equals("es")) {
			announcement = "Bienvenido_es.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("en")) {
			announcement = "Welcome_en.wav";
		}
		if (languageAtribute.getLanguageAttribute().equals("pt")) {
			announcement = "Bem_vindo_pt.wav";
		}
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

		PlayItem playItem = null;

		/*
		 * PlayWelcome == null reproduce el mensaje de bienvenida
		 * "Bienvenido.wav" es el resultado del stringBuilder
		 */
		playItem = MediaFactory.createPlayItem().setInterruptible(true)
				.setIterateCount(1).setSource(sb.toString());

		final MediaService mediaService = MediaFactory.createMediaService();
		final Participant participant = call.getCallingParty();
		final MyMediaListener myMediaListener = new MyMediaListener(call);
		mediaService.play(participant, playItem, myMediaListener);
	}
}
