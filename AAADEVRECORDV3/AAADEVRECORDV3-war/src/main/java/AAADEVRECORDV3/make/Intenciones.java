package AAADEVRECORDV3.make;

import java.net.URISyntaxException;

import AAADEVRECORDV3.util.LanguageAttribute;

import com.avaya.collaboration.businessdata.api.NoAttributeFoundException;
import com.avaya.collaboration.businessdata.api.ServiceNotFoundException;
import com.avaya.collaboration.call.Call;

public class Intenciones {
	public void DefinirIntencion(Call call, String intent)
			throws URISyntaxException, NoAttributeFoundException,
			ServiceNotFoundException {

		String announcement = null;
		Boolean playWelcome = false;
		PlayThanks playThanks = new PlayThanks();
		LanguageAttribute languageAttribute = new LanguageAttribute(call);
		if (intent.equals("CANCELACIONES") || intent.equals("CANCELLATIONS") || intent.equals("CANCELAMENTOS")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Gracias_es_Cancelaciones.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Thanks_en_Cancellations.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Obrigado_pt_cancelamentos.wav";
				playThanks.playThanks(call, playWelcome, announcement);
			}

		}
		if (intent.equals("FACTURACION") || intent.equals("BILLING") || intent.equals("FATURAMENTO")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Gracias_es_Facturacion.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Thanks_en_Billing.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Obrigado_pt_faturamento.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}

		}
		if (intent.equals("HELP_DESK") || intent.equals("SERVICIO_TECNICO") || intent.equals("SUPORTE_TECNICO")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Gracias_es_Soporte_Tecnico.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Thanks_en_Technical_Support.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Obrigado_pt_suporte_tecnico.wav";
				playThanks.playThanks(call, playWelcome, announcement);
			}

		}
		if (intent.equals("SERVICIO_A_CLIENTES") || intent.equals("CUSTOMER_SERVICE") || intent.equals("ATENDIMENTO_AO_CLIENTE") || intent.equals(" ") 
				|| intent.equals("Irrelevant") || intent.equals("IRRELEVANT")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Gracias_es_Servicio_A_Clientes.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Thanks_en_Customer_Service.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Obrigado_pt_atendimento_ao_cliente.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}

		}
		if (intent.equals("VENTAS") || intent.equals("SALES") || intent.equals("VENDAS")) {
			if (languageAttribute.getLanguageAttribute().equals("es")) {

				announcement = "Gracias_es_Ventas.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("en")) {

				announcement = "Thanks_en_Sales.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}
			if (languageAttribute.getLanguageAttribute().equals("pt")) {

				announcement = "Obrigado_pt_vendas.wav";
				playThanks.playThanks(call, playWelcome, announcement);

			}

		}

	}
}
