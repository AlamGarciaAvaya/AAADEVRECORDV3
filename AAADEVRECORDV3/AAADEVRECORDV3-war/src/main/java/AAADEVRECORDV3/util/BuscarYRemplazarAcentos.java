package AAADEVRECORDV3.util;

public class BuscarYRemplazarAcentos {

	public BuscarYRemplazarAcentos() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String Español(String content) {
		StringBuilder sb = new StringBuilder();
		String[] acentosEspeciales = { "á", "é", "í", "ó", "ú", "ü" };
		char[] vector2 = content.toCharArray();
		try {
			for (int a = 0; a < content.length(); a++) {
				String params = String.valueOf(vector2[a]);
				if (acentosEspeciales[0].equalsIgnoreCase(params)) {

					sb.append("&#225;");
					continue;

				}
				if (acentosEspeciales[1].equalsIgnoreCase(params)) {

					sb.append("&#233;");
					continue;

				}
				if (acentosEspeciales[2].equalsIgnoreCase(params)) {

					sb.append("&#237;");
					continue;

				}
				if (acentosEspeciales[3].equalsIgnoreCase(params)) {

					sb.append("&#243;");
					continue;

				}
				if (acentosEspeciales[4].equalsIgnoreCase(params)) {

					sb.append("&#250;");
					continue;

				}
				if (acentosEspeciales[5].equalsIgnoreCase(params)) {

					sb.append("&#252;");
					continue;

				}

				sb.append(params);
			}

		} catch (Exception e) {
			String error = "Error Buscar y Remplazar : " + e.toString();
			return error;
		}
		return sb.toString();
	}

	public String Portugues(String content) {
		String[] acentosEspeciales = { "á", "é", "í", "ó", "ú", "â", "ê", "ô",
				"à", "ã", "õ", "ç" };
		char[] vector2 = content.toCharArray();

		StringBuilder sb = null;
		sb = new StringBuilder();
		try {
			for (int a = 0; a < content.length(); a++) {
				String params = String.valueOf(vector2[a]);
				if (acentosEspeciales[0].equalsIgnoreCase(params)) {

					sb.append("&#225;");
					continue;

				}
				if (acentosEspeciales[1].equalsIgnoreCase(params)) {

					sb.append("&#233;");
					continue;

				}
				if (acentosEspeciales[2].equalsIgnoreCase(params)) {

					sb.append("&#237;");
					continue;

				}
				if (acentosEspeciales[3].equalsIgnoreCase(params)) {

					sb.append("&#243;");
					continue;

				}
				if (acentosEspeciales[4].equalsIgnoreCase(params)) {

					sb.append("&#250;");
					continue;

				}
				if (acentosEspeciales[5].equalsIgnoreCase(params)) {

					sb.append("&#226;");
					continue;

				}
				if (acentosEspeciales[6].equalsIgnoreCase(params)) {

					sb.append("&#234;");
					continue;

				}
				if (acentosEspeciales[7].equalsIgnoreCase(params)) {

					sb.append("&#244;");
					continue;

				}
				if (acentosEspeciales[8].equalsIgnoreCase(params)) {

					sb.append("&#224;");
					continue;

				}
				if (acentosEspeciales[9].equalsIgnoreCase(params)) {

					sb.append("&#227;");
					continue;

				}
				if (acentosEspeciales[10].equalsIgnoreCase(params)) {

					sb.append("&#245;");
					continue;

				}
				if (acentosEspeciales[11].equalsIgnoreCase(params)) {

					sb.append("&#231;");
					continue;

				}

				sb.append(params);
			}

		} catch (Exception e) {
			String error = "Error Buscar y Remplazar : " + e.toString();
			return error;
		}
		return sb.toString();
	}
}
