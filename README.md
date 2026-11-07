# GoCepat: Jawaban Ujian Data Structures and Algorithm Analysis

Kode Java untuk studi kasus aplikasi transportasi online **GoCepat**:

- **Soal 1 (LO1)**: hierarki class `Kendaraan` (polymorphism), alternatif `enum`, relasi aggregation vs composition pada `Trip`, dan penambahan `SepedaListrik` tanpa mengubah `Trip`.
- **Soal 3 (LO3)**: graph peta kota berbasis adjacency list dari array biasa, pencarian driver terdekat dengan BFS (`bfsCariTerdekat`), versi berbobot dengan Dijkstra, dan eksperimen skala 1 juta persimpangan (pendukung Soal 2).

Semua struktur data dibuat sendiri dengan array Java. Tidak ada `import` sama sekali, sehingga tidak ada kelas `java.util` / Collections Framework yang dipakai.

## Struktur folder

```
src/
  soal1/                     Soal 1 (LO1)
    Kendaraan.java           interface: hitungTarif(double jarakKm)
    ValidasiKendaraan.java   validasi plat dan jarak (kelas utilitas package-private)
    Motor.java, Mobil.java, MobilListrik.java
    SepedaListrik.java       jenis baru untuk Soal 1c
    JenisKendaraan.java      alternatif enum dengan constant-specific method
    Driver.java, Penumpang.java, RincianTarif.java, Trip.java
    FormatAngka.java         format Rupiah/km tanpa Locale
    MainSoal1.java           program demo Soal 1
  soal3/                     Soal 3 (LO3) + eksperimen Soal 2
    AntrianInt.java          queue circular buffer berbasis array
    GraphPetaKota.java       adjacency list int[][] + bfsCariTerdekat
    HasilPencarian.java      hasil BFS (driver, hop, rute, urutan kunjungan)
    MainSoal3.java           simulasi Soal 3b (14 simpul, 4 driver)
    MinHeapJarak.java        min-heap biner berbasis array
    GraphBerbobot.java       graph berbobot km + Dijkstra (Soal 3c)
    MainSoal3cBerbobot.java  demo BFS vs Dijkstra (Soal 3c)
    EksperimenSkalaBesar.java  eksperimen 1 juta simpul (Soal 2b dan 2c)
output/                      output asli hasil menjalankan program
JAWABAN_UJIAN_DSA.md         dokumen jawaban lengkap
```

## Cara menjalankan

Prasyarat: JDK 8 atau lebih baru (diuji dengan Oracle JDK 21.0.11; kode juga lolos `javac --release 8`).

Jalankan dari folder ini (Windows PowerShell/CMD, Linux, atau macOS):

```
javac -d out src/soal1/*.java src/soal3/*.java

java -cp out soal1.MainSoal1
java -cp out soal3.MainSoal3
java -cp out soal3.MainSoal3cBerbobot
java -cp out soal3.EksperimenSkalaBesar
```

`EksperimenSkalaBesar` membangun graph 1 juta simpul; di mesin uji ia selesai dalam sekitar 5 detik dan tetap berjalan dengan heap dibatasi `-Xmx256m`. Angka waktunya bergantung pada mesin.

## Mengunggah ke GitHub (untuk link di jawaban)

1. Buat repository baru di https://github.com/new (misalnya `gocepat-dsa`).
2. Dari folder ini jalankan:

```
git init
git add .gitignore README.md JAWABAN_UJIAN_DSA.md src output
git commit -m "Jawaban ujian DSA: GoCepat"
git branch -M main
git remote add origin https://github.com/USERNAME/gocepat-dsa.git
git push -u origin main
```

3. Ganti placeholder link di bagian atas `JAWABAN_UJIAN_DSA.md` dengan URL repository Anda.

Pertimbangkan visibilitas repository: repository publik bisa dilihat mahasiswa lain. Jika dosen mengizinkan, gunakan repository private lalu undang akun GitHub dosen sebagai collaborator.
