# 🎬 Sinema Bilet Sistemi - Proje Rehberi

Bu doküman, projeye yeni katılanlar için **Sıfırdan Başlangıç Rehberi** olarak hazırlanmıştır. Projenin ne olduğunu, nasıl çalıştığını ve teknolojilerini basitçe açıklar.

---

## 🧐 Bu Proje Nedir?

Bu proje, bir sinema bilet rezervasyon sisteminin **modern yazılım mimarisi** olan **Microservices** (Mikroservisler) kullanılarak geliştirilmiş halidir.

**Eski Yöntem (Monolithic):** Eskiden tüm özellikler (Kullanıcı kaydı, Film ekleme, Bilet alma) tek bir devasa projenin içindeydi. Bir yer bozulursa, tüm sistem çökebilirdi.

**Bizim Yöntemimiz (Microservices):** Biz sistemi küçük parçalara böldük. Her parça (Servis) sadece kendi işini yapar.
- Kullanıcı işleri için ayrı bir servis.
- Film listesi için ayrı bir servis.
- Koltuk seçimi için ayrı bir servis.
- Rezervasyon için ayrı bir servis.

Bu sayede proje daha düzenli, ölçeklenebilir ve sağlam olur.

---

## 🏗️ Projenin Parçaları (Servisler)

Sistemi bir yapboz gibi düşünebilirsiniz. İşte parçalar:

### 1. ⚙️ Altyapı Servisleri (Sistemin Kalbi)
Bunlar kullanıcıların görmediği ama sistemin çalışması için zorunlu olan parçalar.
- **Eureka Server (Port 8761):** Servislerin "Telefon Rehberi"dir. Hangi servis hangi adreste çalışıyor, bunu bilir. Diğer servisler açılınca buraya "Ben geldim" diye kayıt olur.
- **Config Server (Port 8888):** Ayarların merkezidir. Tüm servislerin ayarları (veritabanı şifresi, port numarası vb.) tek bir yerde tutulur. Servisler açılırken "Benim ayarlarım nedir?" diye buraya sorar.
- **API Gateway (Opsiyonel):** Sistemin giriş kapısıdır. Dışarıdan gelen istekleri doğru servise yönlendirir.

### 2. 🧱 İş Servisleri (Backend)
Gerçek işi yapan Java uygulamalarıdır.
- **User Service (Port 8085):** Kullanıcı kayıt olma, giriş yapma işlerine bakar. Güvenlik için JWT (Kimlik Kartı) üretir.
- **Catalog Service (Port 8082):** Sinemadaki filmleri ve hangi salonda, saat kaçta oynadığını listeler.
- **Seat Service (Port 8083):** Salonların koltuk düzenini tutar. Hangi koltuk boş, hangisi dolu, hangisi kilitli (biri alıyor) bilgisini yönetir.
- **Booking Service (Port 8084):** Rezervasyon işlemini yönetir. Ödeme (simülasyon) alır ve bileti oluşturur.

### 3. 🖥️ Ön Yüz (Frontend)
- **Frontend (Port 5173):** Kullanıcıların gördüğü web sitesidir. React ile yazılmıştır. Arka plandaki servislerle konuşarak verileri ekrana getirir.

---

## 🛠️ Kullanılan Teknolojiler

- **Dil:** Java 17
- **Framework:** Spring Boot 3 (En popüler Java framework'ü)
- **Veritabanı:** PostgreSQL (Her servisin kendi veritabanı var, birbirlerinin verisine karışmazlar!)
- **Container:** Docker (Tüm sistemi paketleyip tek komutla çalıştırmak için)
- **Frontend:** React, Vite, Tailwind CSS

---

## 🚀 Sistemi Nasıl Çalıştırırım? (Adım Adım)

Projenin tamamını bilgisayarında çalıştırmak için şu adımları izle:

### Adım 1: Hazırlık
Bilgisayarınızda aşağıdaki araçların kurulu olması gerekmektedir. Eğer kurulu değilse linklere tıklayarak indirip kurabilirsiniz (Standart kurulum yeterlidir):

1.  **Docker Desktop** (Backend servisleri için):
    *   [İndir (Windows/Mac/Linux)](https://www.docker.com/products/docker-desktop/)
    *   *Kurduktan sonra Docker'ı açıp arka planda çalıştığından emin olun.*

2.  **Node.js** (Frontend için):
    *   [İndir (LTS Sürümünü seçin)](https://nodejs.org/en/download/)

3.  **Git** (Projeyi indirmek için):
    *   [İndir](https://git-scm.com/downloads)

### Adım 2: Backend'i Başlat (Docker)
Terminali (komut satırını) aç ve proje klasörüne gir. Şu komutu yaz:

```bash
docker compose up --build
```

Bu komut:
1.  Veritabanlarını oluşturur.
2.  Java projelerini derler (`.jar` dosyası yapar).
3.  Her şeyi sırayla çalıştırır.
*Not: İlk defa çalıştırıyorsan indirmeler 5-10 dakika sürebilir. Sabırlı ol.*

### Adım 3: Frontend'i Başlat
Yeni bir terminal penceresi daha aç. `frontend` klasörüne git ve şu komutları yaz:

```bash
cd frontend
npm install   # Gerekli paketleri indirir
npm run dev   # Siteyi başlatır
```

### Adım 4: Test Et
Tarayıcını aç ve **http://localhost:5173** adresine git.
1.  **Register** sayfasından yeni bir kullanıcı oluştur.
2.  Giriş yap.
3.  Bir film seç, koltuk beğen ve "Book" butonuna bas.
4.  Tebrikler! Mikroservis mimarisini çalıştırdın.

---

## ❓ Sık Karşılaşılan Sorunlar

- **"Connection Refused" Hatası:** Servisler henüz tam açılmamış olabilir. Docker loglarında "Started ...Application" yazısını görene kadar bekle (1-2 dakika).
- **Veritabanı Hatası:** Bazen Docker takılabilir. `docker compose down -v` komutuyla her şeyi silip temiz bir sayfa açabilirsin.

---

**Özet:** Bu proje, modern yazılım dünyasında "büyük sistemler nasıl yönetilir?" sorusunun cevabıdır. Her parça bağımsızdır, bu sayede sistem esnek ve güçlüdür.
