package AAADEVRECORDV3.Http.MediaListeners;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;




import service.AAADEVRECORDV3.MyEmailSender;
import AAADEVRECORDV3.Bean.Usuario;
import AAADEVRECORDV3.Http.GetFileAccess;
import AAADEVRECORDV3.Http.PlayAnnouncement.PlayNoUser;
import AAADEVRECORDV3.Http.PlayAnnouncement.PlayWelcomeVerify;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

public class MediaListenerRingOut extends MediaListenerAbstract{
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	public MediaListenerRingOut(final Call call) {
		this.call = call;
	}

	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if(cause == PlayOperationCause.COMPLETE){
			logger.info("MediaListenerRingOut PlayOperationCause.COMPLETE");
			if(call.isCalledPhase()){
				try{
					GetFileAccess get = new GetFileAccess();
					String jsonData = get.fileHttp();
					JSONArray jobj = new JSONArray(jsonData);
					for (int i = 0; i < jobj.length(); i++) {
						String phone = jobj.getJSONObject(i).has("phone") ? jobj.getJSONObject(i).getString("phone") : "null";
						Participant callingParticipant = call.getCallingParty();
						if (phone.equals(callingParticipant.getHandle())) {
							//Se ha encontrado coincidencia en el teléfono
							String train = jobj.getJSONObject(i).getString("train");
							if (train.equals("yes")) {
								//EL USUARIO ESTÁ ENTRENADO.
								try {	
									String userName = jobj.getJSONObject(i).has("username")?jobj.getJSONObject(i).getString("username"):"";
					                String name = jobj.getJSONObject(i).has("name") ? jobj.getJSONObject(i).getString("name") : "";
					                String verbiouser = jobj.getJSONObject(i).has("verbiouser") ? jobj.getJSONObject(i).getString("verbiouser") : "";
					                String fecha = jobj.getJSONObject(i).has("fecha") ? jobj.getJSONObject(i).getString("fecha") : "";
					                String hora = jobj.getJSONObject(i).has("hora") ? jobj.getJSONObject(i).getString("hora") : "";
					                phone = jobj.getJSONObject(i).has("phone") ? jobj.getJSONObject(i).getString("phone") : "";
					                train = jobj.getJSONObject(i).has("train") ? jobj.getJSONObject(i).getString("train") : "";
					                String country = jobj.getJSONObject(i).has("country") ? jobj.getJSONObject(i).getString("country") : "";
					                Boolean cajaSocialExists = jobj.getJSONObject(i).has("Caja_Social")?true:false;
					                String cuenta = "";
					                String saldo = "";
					                ArrayList<String> históricoList = null;
					                if(cajaSocialExists){
					                    JSONObject cajaSocial = jobj.getJSONObject(i).getJSONObject("Caja_Social");
					                    cuenta = cajaSocial.getString("Cuenta_Caja_Social");
					                    saldo = cajaSocial.getString("Saldo_Caja_Social");
					                    JSONArray cajaSocialArray = cajaSocial.getJSONArray("Historico_Caja_Social");
					                    históricoList = new ArrayList<String>();
					                    for (int j = 0; j <= cajaSocialArray.length() - 1; j++) {
					                        históricoList.add(cajaSocialArray.getString(j));
					                    }
					                }
					                
							        Usuario usuario = new Usuario(jobj.getJSONObject(i).getInt("id"), name, verbiouser, userName, fecha, hora, phone, train, country, cuenta, históricoList, saldo);										
									PlayWelcomeVerify play = new PlayWelcomeVerify(call, usuario);	
									play.welcomeVerify();
									break;
								} catch (URISyntaxException e) {
									logger.info("URISyntaxExceptions "+ e.toString());
								}
							}else {
								//EL USUARIO NO ESTÁ ENTRENADO (DROP CALL).
								//ACCION PENDIENTE.
								logger.error("EL USUARIO NO ESTA ENTRENADO");
								PlayNoUser play = new PlayNoUser(call);
								play.userNotRegistered();
								break;
							}
						}
						if (i == (jobj.length() - 1)) {
							//NO SE ENCONTRARON COINCIDENCIAS CON EL TELÉFONO, SE VERIFICA POR NÚMERO DE CUENTA.
							PlayNoUser play = new PlayNoUser(call);
							play.userNotRegistered();
							break;

						}
					}
					
				}catch(Exception e){
					logger.error("CallListener "+e.toString());
					new MyEmailSender().sendErrorByEmail("CallListener "+e.toString(), call);
				}
			}else {
				logger.info("Snap-in sequenced in calling phase");
				call.allow();
			}
		}
		if(cause == PlayOperationCause.FAILED){
			logger.info("MediaListenerRingOut PlayOperationCause.FAILED");
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.info("MediaListenerRingOut PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerRingOut PlayOperationCause.STOPPED");
		}
	}
}
