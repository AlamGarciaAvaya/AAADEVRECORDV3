package AAADEVRECORDV3.util;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
public class Constants {
	public static final String CLOUD_PROVIDER = "cloudProvider";
	public static final String AUDIOS_FOLDER = "folder";
	
	public static final String GET_FILE_ACCESS = "http://breeze2-132.collaboratory.avaya.com/services/AAADEVLOGGER/InputLogger/web/LogIn/Access.txt";
	
	public final static String SECRET_KEY = "AmericasInternationalPoCDevelopmentTeam";
	public final static String SALT = "MexicoTeam";
	
	public static final String IDIOMA_OPCION = "idioma";
	public static final String AGENT_PHONE = "agentPhone";
	public static final String EMAIL = "email";
	public static final String NLU_USER_NAME = "NLUUserName";
	public static final String NLU_PASSWORD = "NLUPassword";
	public static final String NLU_CURRENT_VERSION = "NLUCurrentVersion";
	public static final String WA_USER_NAME = "WAUserName";
	public static final String WA_WORK_SPACE_ID = "WAWorkSpaceId";
	public static final String WA_PASSWORD = "WAPassword";
	public static final String WA_CURRENT_VERSION = "WACurrentVersion";
	public static final String LT_API_KEY = "ltapikey";
	public static final String GOOGLE_CLOUD_API_KEY = "GoogleCloudSpeechToText";
	public static final String VPS_FQDN = "VPSFQDN";
	public static final String EMAIL_FROM = "emailFrom";
	public static final String SCORE_VOICE_RECOGNITION = "scorevoicerecognition";
	public static final String IBM_TTS_USER_NAME = "ttsWatsonUserName";
	public static final String IBM_TTS_PASSWORD = "ttsWatsonPassword";
	
	
	public static String PATH_TO_AUDIOS = getPathWebApp() + "/Audios/";

	private static String getPathWebApp() {
		String realPath = getApplcatonPath();
		String[] split = realPath.split("/");
		StringBuilder path = new StringBuilder();
		for (int k = 1; k < split.length - 1; k++) {
			path.append("/");
			path.append(split[k]);
		}
		return path.toString();
	}

	private static String getApplcatonPath() {
		CodeSource codeSource = Constants.class.getProtectionDomain()
				.getCodeSource();
		File rootPath = null;
		try {
			rootPath = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			return e.toString();
		}
		return rootPath.getParentFile().getPath();
	}

	
}
