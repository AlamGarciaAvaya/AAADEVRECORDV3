package AAADEVRECORDV3.Http.Servlets;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;




import AAADEVRECORDV3.util.RecordingData;

import com.avaya.collaboration.util.logger.Logger;

@WebServlet("/StoreRecordingServlet/*")
@MultipartConfig
public class StoreRecordingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private transient final Logger logger = Logger
			.getLogger(StoreRecordingServlet.class);

	public StoreRecordingServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("StoreRecordingServlet doPost ENTER");
		try {

			final File audioFile = getFile(request, response);
			final boolean isDeleted = audioFile.delete();
			if (isDeleted) {
				logger.info("StoreRecordingServlet doPost previous record file is removed.");
			}

			/*
			 * saveAudioFile escribe un archivo, tratándolo como bytes en lugar
			 * de texto
			 */
			final FileOutputStream saveAudioFile = new FileOutputStream(
					audioFile);

			/*
			 * Part This class represents a part or form item that was received
			 * within a multipart/form-data POST request. El método
			 * request.getParts () devuelve colecciones de todos los objetos de
			 * la Parte.
			 */
			final Part audioPartOfFile = request.getPart("rec_data");

			// ESTA PARTE ES IGUAL

			/*
			 * InputStream Esta clase abstracta es la superclase de todas las
			 * clases que representan un flujo de entrada de bytes. Las
			 * aplicaciones que necesitan definir una subclase de InputStream
			 * siempre deben proporcionar un método que devuelva el siguiente
			 * byte de entrada getInputStream() Gets the content of this part as
			 * an InputStream
			 */
			final InputStream audioInput = audioPartOfFile.getInputStream();

			/*
			 * Define el tamaño que requiere el arreglo de Bytes
			 */
			final byte audioBytes[] = new byte[(int) audioPartOfFile.getSize()];

			/*
			 * public abstract int read() throws IOException Reads the next byte
			 * of data from the input stream. The value byte is returned as an
			 * int in the range 0 to 255. If no byte is available because the
			 * end of the stream has been reached, the value -1 is returned.
			 * This method blocks until input data is available, the end of the
			 * stream is detected, or an exception is thrown.
			 */
			while ((audioInput.read(audioBytes)) != -1) {
				/*
				 * A ByteArrayInputStream contains an internal buffer that
				 * contains bytes that may be read from the stream. An internal
				 * counter keeps track of the next byte to be supplied by the
				 * read method.
				 */
				InputStream byteAudioStream = new ByteArrayInputStream(
						decode(audioBytes));
				/*
				 * AudioFormat is the class that specifies a particular
				 * arrangement of data in a sound stream
				 */
				final AudioFormat audioFormat = getAudioFormat();
				/*
				 * An audio input stream is an input stream with a specified
				 * audio format and length. The length is expressed in sample
				 * frames, not bytes Constructs an audio input stream that has
				 * the requested format and length in sample frames, using audio
				 * data from the specified input stream.
				 */
				AudioInputStream audioInputStream = new AudioInputStream(
						byteAudioStream, audioFormat, audioBytes.length);

				if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE,
						audioInputStream)) {
					/*
					 * 
					 * La clase AudioSystem actúa como el punto de entrada a los
					 * recursos del sistema de audio muestreado. Writes a stream
					 * of bytes representing an audio file of the specified file
					 * type to the output stream provided.
					 */

					AudioSystem.write(audioInputStream,
							AudioFileFormat.Type.WAVE, saveAudioFile);

				}

			}
			// TERMINA LA PARTE IGUAL

			audioInput.close();
			/*
			 * Flushes this output stream and forces any buffered output bytes
			 * to be written out.
			 */
			saveAudioFile.flush();
			saveAudioFile.close();
			logger.info("recording saved as " + audioFile.getAbsolutePath());
			RecordingData.INSTANCE.setRecordingFilename(audioFile.getName());


		} catch (final Exception e) {
			response.setContentType("text/plain");
			PrintWriter pw = response.getWriter();
			pw.write("doPost exception=" + e);
		}
		
		logger.info("StoreRecordingServlet doPost EXIT");
	}

	@Override
	protected void doPut(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		final File playPrevRecordedFile = getFile(request, response);

		if (playPrevRecordedFile.exists()) {
			response.setContentType("text/plain");
			PrintWriter pw = response.getWriter();
			pw.write("EXISTS");
		}
		logger.info("StoreRecordingServlet doPut EXIT");
	}

	@Override
	protected void doDelete(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		final ServletContext callableServiceServletContext = getServletContext();
		final String contextPath = callableServiceServletContext
				.getRealPath("/");
		final String filename = RecordingData.INSTANCE.getRecordingFilename();
		if (filename != null) {
			final File audioFile = new File(contextPath, filename);
			logger.info("Is file exstis " + audioFile.exists());
			if (audioFile.exists()) {
				logger.info(audioFile.getAbsolutePath());
			}
			final boolean isDeleted = audioFile.delete();
			if (isDeleted) {
				RecordingData.INSTANCE.setRecordingFilename(null);
				logger.info("file deleted " + audioFile.getAbsolutePath());
			}
		}
		logger.info("StoreRecordingServlet doDelete EXIT");
	}

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		logger.info("StoreRecordingServlet doGet ENTER");

		/*
		 * ServletContext Defines a set of methods that a servlet uses to
		 * communicate with its servlet container Returns the name of this web
		 * application corresponding to this ServletContext as specified in the
		 * deployment descriptor for this web application by the display-name
		 * element.
		 */
		final ServletContext callableServiceServletContext = getServletContext();

		logger.info(callableServiceServletContext);

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

		logger.info(contextPath);

		/*
		 * Obtiene el nombre del último archivo de audio guardado.
		 */
		final String filename = RecordingData.INSTANCE.getRecordingFilename();


		if (filename != null) {
			/*
			 * File(String parent, String child) Creates a new File instance
			 * from a parent pathname string and a child pathname string.
			 */
			final File audioFile = new File(contextPath, filename);
			if (audioFile.exists()) {
				/*
				 * A MIME attachment with the content type
				 * "application/octet-stream" is a binary file.
				 */
				response.setContentType("APPLICATION/OCTET-STREAM");
				/*
				 * n una respuesta HTTP regular, el encabezado
				 * Content-Disposition indica si el contenido se espera que se
				 * muestre en línea en el navegador, esto es, como una o como
				 * parte de una página web, o como un archivo adjunto, que se
				 * puede descargar y guardar localmente. Content-Disposition:
				 * inline Content-Disposition: attachment Content-Disposition:
				 * attachment; filename="filename.jpg"
				 * 
				 * response.setHeader("Content-Disposition",
				 * "attachment; filename=\"" + filename + "\"");
				 */
				response.setHeader("Content-Disposition", "inline");
				/*
				 * 
				 * Un FileInputStream obtiene bytes de entrada de un archivo en
				 * un sistema de archivos. Los archivos disponibles dependen del
				 * entorno del host. FileInputStream(File file) Crea un
				 * FileInputStream abriendo una conexión a un archivo real, el
				 * archivo nombrado por el archivo objeto de archivo en el
				 * sistema de archivos.
				 */
				final FileInputStream audioFileStream = new FileInputStream(
						audioFile);

				/*
				 * Returns a PrintWriter object that can send character text to
				 * the client.
				 */
				final PrintWriter out = response.getWriter();

				int data = 0;
				while ((data = audioFileStream.read()) != -1) {
					/*
					 * write(int c) Writes a single character.
					 */
					out.write(data);
				}

				out.close();
				audioFileStream.close();
			} else {
				response.setStatus(404);
				response.sendError(404, "audio file does not exist");
			}

		}
		logger.info("StoreRecordingServlet doGet EXIT");
	}

	public File getFile(final HttpServletRequest request,
			final HttpServletResponse response) {

		final ServletContext callableServiceServletContext = getServletContext();
		final String contextPath = callableServiceServletContext
				.getRealPath("/");
		final String filename = request.getPathInfo();
		logger.info("StoreRecordingServlet store recording as " + filename);
		return new File(contextPath, filename);

	}

	public static byte[] decode(byte[] encodedAudioBytes)
			throws MessagingException, IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				encodedAudioBytes);
		InputStream b64InputStream = MimeUtility.decode(byteArrayInputStream,
				"base64");

		byte[] tmpAudioBytes = new byte[encodedAudioBytes.length];
		int numberOfBytes = b64InputStream.read(tmpAudioBytes);
		byte[] decodedAudioBytes = new byte[numberOfBytes];

		System.arraycopy(tmpAudioBytes, 0, decodedAudioBytes, 0, numberOfBytes);

		return decodedAudioBytes;
	}
	
	public static String encoder(String audioPath) {
		String base64Image = "";
		File file = new File(audioPath);
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			// Reading a Image file from file system
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			base64Image = Base64.getEncoder().encodeToString(imageData);
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
		return base64Image;
	}

	/*
	 * Avaya recommends that audio played by Avaya Aura MS be encoded as 16-bit,
	 * 8 kHz, single channel, PCM files. Codecs other than PCM or using higher
	 * sampling rates for higher quality recordings can also be used, however,
	 * with reduced system performance. Multiple channels, like stereo, are not
	 * supported.
	 * 
	 * @see Using Web Services on Avaya Aura Media Server Release 7.7, Issue 1,
	 * August 2015 on support.avaya.com
	 */
	private AudioFormat getAudioFormat() {
		final float sampleRate = 8000.0F;
		// 8000,11025,16000,22050,44100
		final int sampleSizeInBits = 16;
		// 8,16
		final int channels = 1;
		// 1,2
		final boolean signed = true;
		// true,false
		final boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	
}
