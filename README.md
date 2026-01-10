# 🎬 Cinema Custom Microservices Project

## 📋 Proje Hakkında (About the Project)
Bu proje, modern bir **Microservices Mimarisi** kullanılarak geliştirilmiş, uçtan uca bir Sinema Bilet Rezervasyon sistemidir. Monolitik yapıların aksine, her bir işlev (Kullanıcı, Film, Koltuk, Rezervasyon) kendi başına çalışan bağımsız servisler olarak tasarlanmıştır.

Projenin amacı, ölçeklenebilir, bakımı kolay ve modern teknolojilerle donatılmış bir mimariyi gerçek dünya senaryosu üzerinde göstermektir.

---

## 🏗️ Mimari Yapı (Architecture)

Sistem aşağıdaki temel bileşenlerden oluşur:

| Servis Adı | Port | Teknoloji | Açıklama |
|/---|---|---|---|
| **Frontend** | `5173` | React, Vite, Tailwind | Kullanıcı arayüzü. Şık ve responsive tasarım. |
| **API Gateway** | `8080` | Spring Cloud Gateway | (Opsiyonel) Tüm trafiği yöneten giriş kapısı. |
| **Eureka Server** | `8761` | Netflix Eureka | Servislerin birbirini bulmasını sağlayan "Telefon Rehberi". |
| **Config Server** | `8888` | Spring Cloud Config | Tüm servislerin ayarlarını tek yerden yönetir. |
| **User Service** | `8085` | Spring Boot, JWT | Kayıt, Giriş ve Güvenlik (Authentication/Authorization) işlemleri. |
| **Catalog Service** | `8082` | Spring Boot, PostgreSQL | Filmler ve Seansların (Showtimes) listelenmesi. |
| **Seat Service** | `8083` | Spring Boot, PostgreSQL | Koltuk düzeni, kilitleme mekanizması ve anlık durum takibi. |
| **Booking Service** | `8084` | Spring Boot, PostgreSQL | Rezervasyon süreci, ödeme simülasyonu ve bilet oluşturma. |

---

## 🚀 Kurulum ve Çalıştırma (Getting Started)

Projeyi çalıştırmak için bilgisayarınızda **Docker** ve **Node.js** yüklü olmalıdır.

### 1. Backend Servislerini Ayağa Kaldırma
Tüm veritabanları ve Java servisleri Docker üzerinde çalışır.

```bash
# Ana proje dizinine gidin
cd cinema-microservices

# Docker Compose ile her şeyi başlatın
docker compose up --build
```
> **Not:** İlk çalıştırma 2-3 dakika sürebilir. Servislerin hazır olduğunu `http://localhost:8761` (Eureka Dashboard) adresinden kontrol edebilirsiniz.

### 2. Frontend Uygulamasını Başlatma
Arayüzü çalıştırmak için ayrı bir terminal açın:

```bash
# Frontend dizinine gidin
cd frontend

# Paketleri yükleyin
npm install

# Uygulamayı başlatın
npm run dev
```
Uygulama şuradan erişilebilir: **http://localhost:5173**

---

## 🎮 Kullanım Senaryosu (Demo Flow)

1.  **Kayıt Ol (Register):** Yeni bir kullanıcı oluşturun. (Şifreler güvenli bir şekilde hashlenir).
2.  **Giriş Yap (Login):** Oluşturduğunuz kullanıcı ile giriş yapın. Size özel bir **JWT Token** üretilir.
3.  **Film Seç:** Vizyondaki filmleri ve seans saatlerini görüntüleyin.
4.  **Koltuk Seç:**
    *   **Yeşil:** Müsait
    *   **Kırmızı:** Dolu
    *   **Mavi:** Sizin seçiminiz
5.  **Rezervasyon Yap (Book):** "Book Ticket" butonuna basın.
    *   Sistem koltuğu geçici olarak kilitler.
    *   Ödemeyi (simüle edilmiş) alır.
    *   Rezervasyonu onaylar.
6.  **Biletlerim (My Bookings):** Aldığınız biletleri listeleyin.

---

## 🛠️ Teknik Detaylar ve Özellikler

*   **Service Discovery:** Servisler birbirlerinin IP adresini bilmez, **Eureka** üzerinden isimleriyle (örn: `http://seat-service`) haberleşirler.
*   **Centralized Configuration:** `application.yml` dosyaları her servisin içinde değil, merkezi bir **Config Repo**'da tutulur.
*   **Distributed Tracing:** Servisler arası hata takibi kolaydır.
*   **Security:** Tüm istekler **JWT (JSON Web Token)** ile korunur. Token olmadan rezervasyon yapılamaz.
*   **Resilience:** Bir servis çökerse (örn: ödeme servisi), sistemin geri kalanı çalışmaya devam eder veya kullanıcıya anlamlı hata döner.

## ❗️ Sorun Giderme (Troubleshooting)

*   **"Connection Refused" Hatası:** Servisler henüz tam açılmamış olabilir. 1 dakika bekleyip sayfayı yenileyin.
*   **Koltuklar Görünmüyor:** `seed_data.py` scripti ile veritabanını tekrar doldurabilirsiniz veya backend loglarını kontrol edin (`docker compose logs seat-service`).
*   **Login Olamıyorum:** Tarayıcı önbelleğini temizleyin veya farklı bir email ile kayıt olmayı deneyin.

---

**Geliştirici:** Eren Durmaz
**Teknolojiler:** Java 17, Spring Boot 3, React, Docker, PostgreSQL
