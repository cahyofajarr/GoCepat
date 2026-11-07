package soal1;

/** Penumpang juga entitas mandiri (punya akun dan riwayat), jadi di-aggregate oleh Trip. */
public class Penumpang {

    private final String idPenumpang;
    private final String nama;

    public Penumpang(String idPenumpang, String nama) {
        if (idPenumpang == null || nama == null) {
            throw new IllegalArgumentException("Data penumpang tidak lengkap");
        }
        this.idPenumpang = idPenumpang;
        this.nama = nama;
    }

    public String getIdPenumpang() {
        return idPenumpang;
    }

    public String getNama() {
        return nama;
    }

    @Override
    public String toString() {
        return nama + " (" + idPenumpang + ")";
    }
}
