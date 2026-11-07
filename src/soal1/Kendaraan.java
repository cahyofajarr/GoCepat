package soal1;

/**
 * Abstraksi semua jenis kendaraan GoCepat.
 *
 * Trip (dan kode klien lain) HANYA bergantung pada interface ini, bukan pada
 * kelas konkret seperti Motor atau Mobil. Inilah yang membuat jenis kendaraan
 * baru bisa ditambahkan tanpa mengubah Trip (lihat Soal 1c).
 *
 * Kontrak hitungTarif (wajib dipatuhi SEMUA implementasi, prinsip Liskov):
 *  1. jarakKm harus angka valid dan >= 0; selain itu lempar IllegalArgumentException.
 *  2. Hasil dalam Rupiah dan tidak pernah negatif.
 *  3. Tarif tidak boleh turun ketika jarak bertambah (monoton tidak turun).
 */
public interface Kendaraan {

    /** Menghitung tarif perjalanan (Rupiah) untuk jarak tertentu (km). */
    double hitungTarif(double jarakKm);

    /** Nama jenis kendaraan untuk ditampilkan, misalnya "Motor". */
    String getJenis();

    /** Plat nomor / nomor unit: identitas unik setiap unit kendaraan. */
    String getPlatNomor();
}
