package soal1;

/**
 * Validasi bersama untuk semua implementasi Kendaraan.
 *
 * Kelas utilitas ini package-private: bukan bagian dari API publik dan bukan
 * bagian dari hierarki tipe. Setiap kelas kendaraan langsung mengimplementasikan
 * Kendaraan, lalu memanggil method di sini supaya aturan validasinya sama.
 */
final class ValidasiKendaraan {

    private ValidasiKendaraan() {
    }

    /** Butir 1 kontrak Kendaraan.hitungTarif: jarak harus angka valid dan >= 0. */
    static void cekJarak(double jarakKm) {
        if (Double.isNaN(jarakKm) || Double.isInfinite(jarakKm) || jarakKm < 0) {
            throw new IllegalArgumentException("Jarak tidak valid: " + jarakKm);
        }
    }

    /** Plat nomor (atau nomor unit) wajib diisi; spasi di awal dan akhir dibuang. */
    static String cekPlatNomor(String platNomor) {
        if (platNomor == null || platNomor.trim().isEmpty()) {
            throw new IllegalArgumentException("Plat nomor wajib diisi");
        }
        return platNomor.trim();
    }
}
