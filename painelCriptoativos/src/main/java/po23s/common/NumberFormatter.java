package po23s.common;

public class NumberFormatter {
    public static String formatarNumero(String valor) {
        try {
            double numero = Double.parseDouble(valor);
            return String.format("%.2f", numero);
        } catch (Exception e) {
            return valor;
        }
    }
}
