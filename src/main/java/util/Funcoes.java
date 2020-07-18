package util;

public class Funcoes {
	
	public static Double removeCifraoRetornaDouble(String texto) {
		return Double.parseDouble(texto.replace("$", ""));
	}

	public static int removeTextoItemsDevolveInt(String texto) {
		return Integer.parseInt(texto.replace(" items", ""));
	}
	
	public static String removeTexto(String texto, String textoParaRemover) {
		texto = texto.replace(textoParaRemover, "");
		return texto;
	}

}
