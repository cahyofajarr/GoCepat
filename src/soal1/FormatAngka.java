package soal1;

/**
 * Utilitas format angka tanpa bergantung pada Locale komputer, supaya output
 * program sama persis di komputer mana pun (misalnya komputer dosen).
 */
public final class FormatAngka {

    private FormatAngka() {
    }

    /** Contoh: 13000.0 -> "Rp13.000" (dibulatkan ke rupiah terdekat). */
    public static String rupiah(double nilai) {
        long angka = Math.round(nilai);
        String digit = Long.toString(Math.abs(angka));
        StringBuilder sb = new StringBuilder();
        int posisi = 0;
        for (int i = digit.length() - 1; i >= 0; i--) {
            sb.append(digit.charAt(i));
            posisi++;
            if (posisi % 3 == 0 && i > 0) {
                sb.append('.');
            }
        }
        return (angka < 0 ? "-Rp" : "Rp") + sb.reverse();
    }

    /** Contoh: 6.5 -> "6,5 km"; 8.0 -> "8 km" (satu angka desimal). */
    public static String km(double nilai) {
        long kaliSepuluh = Math.round(nilai * 10);
        long bulat = kaliSepuluh / 10;
        long desimal = Math.abs(kaliSepuluh % 10);
        return desimal == 0 ? bulat + " km" : bulat + "," + desimal + " km";
    }
}
