package soal3;

/**
 * Demo Soal 3c: mengapa BFS (jumlah hop) tidak cukup ketika panjang jalan (km)
 * diperhitungkan, dan bagaimana Dijkstra memperbaikinya.
 * Peta yang SAMA dibangun dua kali: tanpa bobot (untuk BFS) dan berbobot km
 * (untuk Dijkstra). Angka jarak ilustratif.
 */
public class MainSoal3cBerbobot {

    private static final String[] NAMA = {
        "Kampus Anggrek",       // 0  lokasi penumpang
        "Simpang Kemanggisan",  // 1
        "Slipi Jaya",           // 2
        "Palmerah",             // 3  driver Dewi
        "Gerbang Tol Tomang",   // 4
        "Exit Tol Meruya",      // 5  driver Rudi
        "Tanjung Duren",        // 6
        "Grogol"                // 7  driver Sari
    };

    private static final String[] NAMA_DRIVER = {null, null, null, "Dewi", null, "Rudi", null, "Sari"};

    private static final int[][] JALAN = {{0, 1}, {1, 2}, {2, 3}, {0, 4}, {4, 5}, {1, 6}, {6, 7}, {7, 4}};
    private static final double[] KM = {0.7, 1.1, 0.9, 1.6, 8.2, 1.4, 2.3, 2.0};

    public static void main(String[] args) {
        GraphPetaKota petaHop = new GraphPetaKota(NAMA.length);
        GraphBerbobot petaKm = new GraphBerbobot(NAMA.length);
        for (int i = 0; i < JALAN.length; i++) {
            petaHop.tambahJalan(JALAN[i][0], JALAN[i][1]);
            petaKm.tambahJalan(JALAN[i][0], JALAN[i][1], KM[i]);
        }

        judul("RUAS JALAN DAN PANJANGNYA");
        for (int i = 0; i < JALAN.length; i++) {
            System.out.println("  " + kiri(simpul(JALAN[i][0]) + " - " + simpul(JALAN[i][1]), 48)
                    + kanan(km(KM[i]), 7) + (KM[i] > 5 ? "   (ruas tol panjang)" : ""));
        }

        boolean[] adalahDriver = new boolean[NAMA.length];
        for (int v = 0; v < NAMA.length; v++) {
            adalahDriver[v] = NAMA_DRIVER[v] != null;
        }
        int asal = 0;

        judul("JARAK DARI PENUMPANG DI " + simpul(asal) + " KE SETIAP DRIVER");
        int[] hopSemua = petaHop.hitungSemuaJarakHop(asal);
        for (int v = 0; v < NAMA.length; v++) {
            if (adalahDriver[v]) {
                boolean[] hanyaDriverIni = new boolean[NAMA.length];
                hanyaDriverIni[v] = true;
                GraphBerbobot.Hasil ke = petaKm.dijkstraCariTerdekat(asal, hanyaDriverIni);
                System.out.println("  " + kiri(NAMA_DRIVER[v] + " di " + simpul(v), 30)
                        + " hop = " + hopSemua[v] + "   jarak jalan terpendek = " + km(ke.getJarakKm()));
            }
        }

        HasilPencarian bfs = petaHop.bfsCariTerdekatDetail(asal, adalahDriver);
        GraphBerbobot.Hasil dijkstra = petaKm.dijkstraCariTerdekat(asal, adalahDriver);
        double kmRuteBfs = panjangRute(petaKm, bfs.getRuteJemput());

        judul("PERBANDINGAN HASIL");
        System.out.println("BFS      (min. hop) -> " + NAMA_DRIVER[bfs.getSimpulDriver()] + ", "
                + bfs.getJumlahHop() + " hop, " + km(kmRuteBfs));
        System.out.println("           rute: " + rute(bfs.getRuteJemput()));
        System.out.println("Dijkstra (min. km)  -> " + NAMA_DRIVER[dijkstra.getSimpulDriver()] + ", "
                + dijkstra.getJumlahHop() + " hop, " + km(dijkstra.getJarakKm()));
        System.out.println("           rute: " + rute(dijkstra.getRuteJemput()));
        System.out.println();
        System.out.println("Pilihan BFS " + satuDesimal(kmRuteBfs / dijkstra.getJarakKm())
                + " kali lebih jauh, karena BFS menganggap setiap ruas jalan berbobot sama (1 hop).");
        System.out.println("Dijkstra berhenti setelah memfinalkan " + dijkstra.getJumlahSimpulFinal() + " dari "
                + NAMA.length + " simpul; simpul yang lebih jauh dari driver terdekat tidak perlu diproses.");
    }

    /** Menjumlahkan panjang setiap ruas pada rute. */
    private static double panjangRute(GraphBerbobot peta, int[] rute) {
        double total = 0;
        for (int i = 0; i + 1 < rute.length; i++) {
            total += peta.panjangJalan(rute[i], rute[i + 1]);
        }
        return total;
    }

    private static String rute(int[] r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < r.length; i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(simpul(r[i]));
        }
        return sb.toString();
    }

    private static String simpul(int v) {
        return "[" + v + "] " + NAMA[v];
    }

    private static String km(double nilai) {
        return satuDesimal(nilai) + " km";
    }

    /** Satu angka desimal dengan koma, tanpa bergantung pada Locale. */
    private static String satuDesimal(double nilai) {
        long kaliSepuluh = Math.round(nilai * 10);
        return (kaliSepuluh / 10) + "," + Math.abs(kaliSepuluh % 10);
    }

    private static void judul(String teks) {
        System.out.println();
        System.out.println(garis('=', 88));
        System.out.println(teks);
        System.out.println(garis('=', 88));
    }

    private static String garis(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static String kiri(String s, int lebar) {
        return String.format("%-" + lebar + "s", s);
    }

    private static String kanan(String s, int lebar) {
        return String.format("%" + lebar + "s", s);
    }
}
