package soal1;

/**
 * PENDEKATAN ALTERNATIF (Soal 1a-i): enum dengan method.
 *
 * Setiap konstanta membawa data (tarif dasar dan tarif per km) serta badan
 * method sendiri (constant-specific method body). Cocok bila himpunan jenis
 * TERTUTUP dan diketahui sejak awal.
 *
 * Perhatikan: enum memodelkan JENIS (kategori) kendaraan, bukan UNIT kendaraan.
 * Instance kelas enum hanya konstanta yang dideklarasikan (satu MOTOR, satu
 * MOBIL, satu MOBIL_LISTRIK). Enum boleh punya state, tetapi state itu berlaku
 * per jenis dan dipakai bersama oleh seluruh program. Data per unit (plat nomor,
 * pemilik, sisa baterai) tidak bisa diwakili oleh instance enum.
 */
public enum JenisKendaraan {

    /** Untuk MOTOR, tarifDasar berarti biaya minimum yang mencakup 5 km pertama. */
    MOTOR("Motor", 13_000, 2_600) {
        @Override
        public double hitungTarif(double jarakKm) {
            cekJarak(jarakKm);
            double kmTambahan = Math.max(0, jarakKm - KM_TERCAKUP_MINIMUM_MOTOR);
            return getTarifDasar() + kmTambahan * getTarifPerKm();
        }
    },

    MOBIL("Mobil", 12_000, 4_000) {
        @Override
        public double hitungTarif(double jarakKm) {
            cekJarak(jarakKm);
            return getTarifDasar() + jarakKm * getTarifPerKm();
        }
    },

    MOBIL_LISTRIK("Mobil Listrik", 20_000, 3_000) {
        @Override
        public double hitungTarif(double jarakKm) {
            cekJarak(jarakKm);
            return getTarifDasar() + jarakKm * getTarifPerKm();
        }
    };

    private static final double KM_TERCAKUP_MINIMUM_MOTOR = 5.0;

    private final String label;
    private final double tarifDasar;
    private final double tarifPerKm;

    JenisKendaraan(String label, double tarifDasar, double tarifPerKm) {
        this.label = label;
        this.tarifDasar = tarifDasar;
        this.tarifPerKm = tarifPerKm;
    }

    /** Abstrak: compiler MEMAKSA setiap konstanta menyediakan rumus tarifnya. */
    public abstract double hitungTarif(double jarakKm);

    public String getLabel() {
        return label;
    }

    public double getTarifDasar() {
        return tarifDasar;
    }

    public double getTarifPerKm() {
        return tarifPerKm;
    }

    private static void cekJarak(double jarakKm) {
        if (Double.isNaN(jarakKm) || Double.isInfinite(jarakKm) || jarakKm < 0) {
            throw new IllegalArgumentException("Jarak tidak valid: " + jarakKm);
        }
    }
}
