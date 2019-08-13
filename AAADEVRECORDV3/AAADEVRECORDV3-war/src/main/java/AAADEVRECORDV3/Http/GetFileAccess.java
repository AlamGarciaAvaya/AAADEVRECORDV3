package AAADEVRECORDV3.Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import AAADEVRECORDV3.util.Constants;

public class GetFileAccess {
	
	public String fileHttp(){
		final String URI = Constants.GET_FILE_ACCESS;
		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpGet getMethod = new HttpGet(URI);
	
			final HttpResponse response = client.execute(getMethod);
	
			final BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));
	
			String line = "";
			final StringBuilder result = new StringBuilder();
			while ((line = inputStream.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} catch (IOException e) {
			return null;
		}
	}
}
