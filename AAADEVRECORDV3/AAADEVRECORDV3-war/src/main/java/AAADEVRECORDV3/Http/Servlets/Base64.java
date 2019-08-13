package AAADEVRECORDV3.Http.Servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AAADEVRECORDV3.util.Encoder;

import com.avaya.collaboration.util.logger.Logger;

@WebServlet("/Base64/*")
@MultipartConfig
public class Base64 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private transient final Logger logger = Logger
			.getLogger(Base64.class);

	public Base64() {
		super();
	}
	
	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		logger.info("Base64 doGet ENTER");
		setAccessControlHeaders(response);
		/*
		 * ServletContext Defines a set of methods that a servlet uses to
		 * communicate with its servlet container Returns the name of this web
		 * application corresponding to this ServletContext as specified in the
		 * deployment descriptor for this web application by the display-name
		 * element.
		 */
		final ServletContext callableServiceServletContext = getServletContext();


		/*
		 * Gets the real path corresponding to the given virtual path. For
		 * example, if path is equal to /index.html, this method will return the
		 * absolute file path on the server's filesystem to which a request of
		 * the form http://<host>:<port>/<contextPath>/index.html would be
		 * mapped, where <contextPath> corresponds to the context path of this
		 * ServletContext.
		 */
		final String contextPath = callableServiceServletContext
				.getRealPath("/");
		

		/*
		 * Obtiene el nombre del último archivo de audio guardado.
		 */
		final String filename = "recordingAAADEVRECORDV3.wav";
		if (filename != null) {
			/*
			 * File(String parent, String child) Creates a new File instance
			 * from a parent pathname string and a child pathname string.
			 */
			final File audioFile = new File(contextPath, filename);
			if (audioFile.exists()) {
				logger.info(audioFile.getAbsoluteFile());
				String base64 = Encoder.encoder(audioFile.getAbsolutePath());

				final PrintWriter out = response.getWriter();
				out.println(base64);

			} else {
				response.setStatus(404);
				response.sendError(404, "audio file does not exist");
			}

		}
		logger.info("Base64 doGet EXIT");
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