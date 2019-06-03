/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.verbio;


/**
 *
 * @author umansilla
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.verbio.util.Encoder;


@WebServlet("/GetBase64")
public class GetBase64 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
	
		setAccessControlHeaders(response);
		final String contextPath = "home/wsuser/web/VerbioAudios/";
                
		/*
		 * Obtiene el nombre del último archivo de audio guardado.
		 */
		final String filename = request.getParameter("audio");
                
		if (filename != null) {
			/*
			 * File(String parent, String child) Creates a new File instance
			 * from a parent pathname string and a child pathname string.
			 */
			final File audioFile = new File(contextPath, filename);
			if (audioFile.exists()) {
				
				String base64 = Encoder.encoder(audioFile.getAbsolutePath());

				final PrintWriter out = response.getWriter();
				out.println(base64);

			} else {
				response.setStatus(404);
				response.sendError(404, "audio file does not exist");
			}

		}
		
	}
	
	//AUTORIZAR CROSS DOMAIN
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setAccessControlHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
    }
    
}