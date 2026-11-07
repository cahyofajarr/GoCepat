package soal3;

/**
 * Simulasi Soal 3b: pencarian driver terdekat GoCepat dengan BFS.
 * Peta di bawah ini ILUSTRATIF (nama tempat di Jakarta hanya dipakai sebagai
 * label), bukan peta jalan yang sebenarnya.
 */
public class MainSoal3 {

    private static final String[] NAMA = {
        "Kampus Anggrek",       // 0
        "Simpang Kemanggisan",  // 1
        "Slipi Jaya",           // 2
        "Palmerah",             // 3
        "Senayan",              // 4
        "Semanggi",             // 5
        "Tomang",               // 6
        "Grogol",               // 7
        "Kebon Jeruk",          // 8
        "Tanjung Duren",        // 9
        "Tanah Abang",          // 10
        "Bundaran HI",          // 11
        "Pulau Pramuka",        // 12 (pulau: tidak terhubung jalan darat ke daratan)
        "Pulau Tidung"          // 13
    };

    /** Daftar ruas jalan dua arah {u, v}. */
    private static final int[][] JALAN = {
        {0, 1}, {0, 8}, {1, 2}, {1, 3}, {1, 9}, {2, 3}, {2, 6}, {2, 5}, {3, 4}, {4, 5},
        {5, 10}, {5, 11}, {6, 7}, {6, 10}, {7, 9}, {8, 9}, {10, 11}, {12, 13}, {3, 5}
    };

    public static void main(String[] args) {
        GraphPetaKota peta = new GraphPetaKota(NAMA.length);
        for (int[] jalan : JALAN) {
            peta.tambahJalan(jalan[0], jalan[1]);
        }
        boolean duplikatDitambah = peta.tambahJalan(1, 0);   // jalan 0-1 sudah ada

        judul("PETA KOTA (adjacency list berbasis array)");
        System.out.println(NAMA.length + " persimpangan, " + peta.getJumlahJalan() + " ruas jalan. "
                + "tambahJalan(1, 0) sekali lagi -> "
                + (duplikatDitambah ? "ditambahkan" : "ditolak karena duplikat"));
        cetakAdjacencyList(peta);

        boolean[] adalahDriver = new boolean[NAMA.length];
        adalahDriver[3] = true;    // Palmerah
        adalahDriver[4] = true;    // Senayan
        adalahDriver[7] = true;    // Grogol
        adalahDriver[11] = true;   // Bundaran HI
        System.out.println();
        System.out.println("Driver tersedia di: " + daftarDriver(adalahDriver));

        judul("PENCARIAN DRIVER TERDEKAT (syarat soal: 2 titik penumpang berbeda)");
        cari(peta, adalahDriver, 0, "Penumpang A");
        cari(peta, adalahDriver, 10, "Penumpang B");

        judul("KASUS TAMBAHAN");
        adalahDriver[3] = false;   // driver Palmerah menerima order A sehingga tidak tersedia
        cari(peta, adalahDriver, 0, "Penumpang C (driver Palmerah sudah sibuk)");
        adalahDriver[3] = true;
        cari(peta, adalahDriver, 4, "Penumpang D (berdiri di lokasi driver)");
        cari(peta, adalahDriver, 12, "Penumpang E (di pulau, tidak terhubung)");
    }

    private static void cari(GraphPetaKota peta, boolean[] adalahDriver, int asal, String label) {
        System.out.println();
        System.out.println(">> " + label + " di " + simpul(asal));

        int driver = peta.bfsCariTerdekat(asal, adalahDriver);                 // method yang diminta soal
        HasilPencarian hasil = peta.bfsCariTerdekatDetail(asal, adalahDriver);  // detail untuk penjelasan

        if (driver == -1) {
            System.out.println("   bfsCariTerdekat(" + asal + ", adalahDriver) = -1"
                    + " -> tidak ada driver yang terjangkau");
        } else {
            System.out.println("   bfsCariTerdekat(" + asal + ", adalahDriver) = " + driver
                    + " -> " + NAMA[driver] + ", jarak " + hasil.getJumlahHop() + " hop");
            System.out.println("   Rute jemput : " + rute(hasil.getRuteJemput()));
        }

        int[] urutan = hasil.getUrutanKunjungan();
        int[] hop = hasil.getHopKunjungan();
        StringBuilder jejak = new StringBuilder();
        for (int i = 0; i < urutan.length; i++) {
            jejak.append(urutan[i]).append("(h").append(hop[i]).append(") ");
        }
        System.out.println("   Urutan BFS  : " + jejak + "-> " + hasil.getJumlahSimpulDiproses()
                + " dari " + peta.getJumlahSimpul() + " simpul diproses");

        // Cek silang dengan BFS penuh: driver yang dipilih harus punya hop minimum.
        int[] jarakSemua = peta.hitungSemuaJarakHop(asal);
        StringBuilder cek = new StringBuilder();
        int minimum = -1;
        for (int v = 0; v < adalahDriver.length; v++) {
            if (adalahDriver[v]) {
                cek.append(NAMA[v]).append('=').append(jarakSemua[v] < 0 ? "-" : String.valueOf(jarakSemua[v]))
                        .append("; ");
                if (jarakSemua[v] >= 0 && (minimum == -1 || jarakSemua[v] < minimum)) {
                    minimum = jarakSemua[v];
                }
            }
        }
        int jumlahSeri = 0;
        for (int v = 0; v < adalahDriver.length; v++) {
            if (adalahDriver[v] && minimum >= 0 && jarakSemua[v] == minimum) {
                jumlahSeri++;
            }
        }
        boolean konsisten = minimum == hasil.getJumlahHop() && driver == hasil.getSimpulDriver();
        System.out.println("   Cek silang  : hop ke tiap driver -> " + cek + "minimum = "
                + (minimum < 0 ? "-" : String.valueOf(minimum)) + (konsisten ? " [OK]" : " [TIDAK KONSISTEN]"));
        if (jumlahSeri > 1) {
            System.out.println("   Catatan     : " + jumlahSeri + " driver seri pada " + minimum
                    + " hop; BFS memilih yang lebih dulu masuk antrian");
        }
    }

    private static void cetakAdjacencyList(GraphPetaKota peta) {
        for (int v = 0; v < peta.getJumlahSimpul(); v++) {
            StringBuilder daftar = new StringBuilder();
            for (int i = 0; i < peta.getDerajat(v); i++) {
                if (i > 0) {
                    daftar.append(", ");
                }
                daftar.append(peta.getTetangga(v, i));
            }
            String catatan = peta.getKapasitas(v) > GraphPetaKota.KAPASITAS_AWAL
                    ? "   <- array tumbuh dari " + GraphPetaKota.KAPASITAS_AWAL + " ke " + peta.getKapasitas(v)
                    : "";
            System.out.println("  [" + kanan(String.valueOf(v), 2) + "] " + kiri(NAMA[v], 20)
                    + " derajat=" + peta.getDerajat(v) + " kapasitas=" + peta.getKapasitas(v)
                    + " : " + daftar + catatan);
        }
    }

    private static String daftarDriver(boolean[] adalahDriver) {
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < adalahDriver.length; v++) {
            if (adalahDriver[v]) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(simpul(v));
            }
        }
        return sb.toString();
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
