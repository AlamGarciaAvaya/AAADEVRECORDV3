package AAADEVRECORDV3.util;

import java.net.URISyntaxException;

import AAADEVRECORDV3.Http.PlayAnnouncement.PlayThanks;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;

public class Intenciones {
	public void DefinirIntencion(Call call, String intent)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException {
		String folderWavs = AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AUDIOS_FOLDER);
		String announcement = null;
		Boolean playWelcome = false;
		PlayThanks playThanks = new PlayThanks();
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		if (intent.equals("CANCELACIONES") || intent.equals("CANCELLATIONS") || intent.equals("CANCELAMENTOS")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {
				
				announcement = "Audios/"+folderWavs+"/ES/Gracias_ES_Cancelaciones.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Audios/"+folderWavs+"/EN/Gracias_EN_Cancelaciones.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Audios/"+folderWavs+"/PT/Gracias_PT_Cancelaciones.wav";
				playThanks.playThanks(call, playWelcome, announcement);
			}

		}
		if (intent.equals("FACTURACION") || intent.equals("BILLING") || intent.equals("FATURAMENTO")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Audios/"+folderWavs+"/ES/Gracias_ES_Facturacion.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Audios/"+folderWavs+"/EN/Gracias_EN_Facturacion.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Audios/"+folderWavs+"/PT/Gracias_PT_Facturacion.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}

		}
		if (intent.equals("HELP_DESK") || intent.equals("SERVICIO_TECNICO") || intent.equals("SUPORTE_TECNICO")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Audios/"+folderWavs+"/ES/Gracias_ES_Soporte_Tecnico.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Audios/"+folderWavs+"/EN/Gracias_EN_Soporte_Tecnico.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Audios/"+folderWavs+"/PT/Gracias_PT_Soporte_Tecnico.wav";
				playThanks.playThanks(call, playWelcome, announcement);
			}

		}
		if (intent.equals("SERVICIO_A_CLIENTES") || intent.equals("CUSTOMER_SERVICE") || intent.equals("ATENDIMENTO_AO_CLIENTE") || intent.equals(" ") 
				|| intent.equals("Irrelevant") || intent.equals("IRRELEVANT")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Audios/"+folderWavs+"/ES/Gracias_ES_Servicio_A_Clientes.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Audios/"+folderWavs+"/EN/Gracias_EN_Servicio_A_Clientes.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Audios/"+folderWavs+"/PT/Gracias_PT_Servicio_A_Clientes.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}

		}
		if (intent.equals("VENTAS") || intent.equals("SALES") || intent.equals("VENDAS")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Audios/"+folderWavs+"/ES/Gracias_ES_Ventas.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Audios/"+folderWavs+"/EN/Gracias_EN_Ventas.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Audios/"+folderWavs+"/PT/Gracias_PT_Ventas.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}

		}

	}
}
