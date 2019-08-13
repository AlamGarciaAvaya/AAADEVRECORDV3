/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.verbio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.avaya.collaboration.util.logger.Logger;

/**
 *
 * @author umansilla
 */
public class VerbioClientRequest {
	private final Logger logger = Logger.getLogger(getClass());

	public JSONObject makeRequest(String payload)
			throws UnsupportedEncodingException, IOException, SSLUtilityException {

		final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
		final SSLContext sslContext = SSLUtilityFactory
				.createSSLContext(protocolTypeAssistant);

		final String URI = "https://avaya:DRNUDUsWh5o3uRdQcZ@cloud2.verbio.com/asv/ws/process";
		final HttpClient client = HttpClients.custom()
				.setSSLContext(sslContext)
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		 
		//final HttpClient client = new DefaultHttpClient();

		final HttpPost postMethod = new HttpPost(URI);

		postMethod.addHeader("Content-Type", "application/json");

		final String messageBody = payload;
		final StringEntity conversationEntity = new StringEntity(messageBody);
		postMethod.setEntity(conversationEntity);

		final HttpResponse response = client.execute(postMethod);

		final BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStream.readLine()) != null) {
			result.append(line);
		}
		logger.info("Respuesta Verbio");
		logger.info(result.toString());

		JSONObject json = new JSONObject(result.toString());
		return json;
	}
}
