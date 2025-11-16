# 💜 LiveVideobes Player – Android Kiosk Digital Signage

O **LiveVideobes Player** é o aplicativo Android oficial da Videobes para mídia indoor, rodando em modo *kiosk* profissional. Ele transforma TVs Android, tablets e TV Boxes em painéis de exibição inteligentes totalmente administrados pelo painel LiveVideobes (via n8n + API).

---

# 🚀 Funcionalidades principais

- ✔ Rodando em **Kiosk Mode** (bloqueia barra, notificações, home e back)  
- ✔ Vídeo de introdução da Videobes (10s) ao ligar  
- ✔ Tela de configuração minimalista (Wi-Fi, pasta, canal)  
- ✔ Admin secreto (3× voltar ou Ctrl+Z)  
- ✔ Reproduz vídeos e imagens em loop aleatório  
- ✔ Playlists totalmente remotas (API)  
- ✔ Cache local (funciona mesmo sem internet)  
- ✔ Exibição de hora e clima (v1 incluído)  
- ✔ Sincronização automática via n8n  
- ✔ Auto-start ao ligar o aparelho

---

# 📁 Estrutura do Projeto

```
/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/videobes/liveplayer/
│   │   │   │       ├── PlayerActivity.kt
│   │   │   │       ├── BootReceiver.kt
│   │   │   │       ├── KioskHelper.kt
│   │   │   │       ├── Prefs.kt
│   │   │   │       └── MediaScanner.kt
│   │   │   ├── res/layout/activity_player.xml
│   │   │   ├── res/drawable/ic_more_vert.xml
│   │   │   ├── res/raw/live_videobes_intro.mp4
│   │   │   └── res/values/themes.xml
│   ├── build.gradle
│
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

# 🛰 Arquitetura

```text
                      Arquitetura Geral

           ┌─────────────────────────────────────────────────┐
           │               Painel LiveVideobes               │
           │  (API + Orquestração via n8n + CDN de mídia)   │
           └───────────────────────┬─────────────────────────┘
                                   │
                                   │ REST (JSON)
                                   ▼
       ┌─────────────────────────────────────────────────────────┐
       │                  LiveVideobes Player (Android)          │
       │---------------------------------------------------------│
       │ • Roda vídeo de intro                                   │
       │ • Gerencia playlists remotas                            │
       │ • Faz cache local (offline)                             │
       │ • Exibe clima e hora                                    │
       │ • Loop ininterrupto                                     │
       │ • Setup inicial + menu secreto                          │
       └─────────────────────────────────────────────────────────┘
```

---

# 🧠 Requisitos técnicos

- **Android mínimo:** 9 (API 28)  
- **Android recomendado:** 10+  
- Compatível com TV Box, Tablets, Smart TVs Android 

---

# 🔌 API – Versão 1.0 (painel LiveVideobes)

## 📍 1. Obter playlist

`GET /api/player/{id}/playlist`

**Exemplo de resposta:**

```json
{
  "player_id": "alkuwait01",
  "playlist_version": 12,
  "poll_interval": 30,
  "timezone": "America/Sao_Paulo",
  "weather_city": "Rio de Janeiro",
  "items": [
    {
      "type": "video",
      "url": "https://cdn.videobes.com/alkuwait/cardapio01.mp4"
    },
    {
      "type": "image",
      "url": "https://cdn.videobes.com/alkuwait/banner1.png",
      "duration_ms": 8000
    },
    {
      "type": "weather",
      "layout": "horizontal-light"
    }
  ]
}
```

---

## 📍 2. Registrar player

`POST /api/player/register`

**Request:**

```json
{
  "serial": "MXQPRO-AB12-FF88",
  "model": "MXQ Pro 4K",
  "version": "1.0",
  "mac_wifi": "44:12:AB:08:17:F1"
}
```

**Response:**

```json
{
  "status": "registered",
  "player_id": "mxqproentrada01"
}
```

---

## 📍 3. Reportar status (opcional)

`POST /api/player/{id}/status`

**Request:**

```json
{
  "current_media": "cardapio01.mp4",
  "uptime_minutes": 551,
  "last_sync": "2025-10-11T02:11:30Z",
  "disk_free": "8.5 GB",
  "playlist_version": 7
}
```

---

# 🔄 Integração com n8n

O painel dispara ações no player:

- upload → atualiza playlist  
- editar canal → atualiza JSON  
- n8n dispara webhook → atualiza player  
- limpeza automática no CDN  
- logs, uptime, falhas e pistas  

---

# 🧱 Build

1. Instale Android Studio Iguana  
2. Clone o repo  
3. `Build > Make Project`  
4. Instale via ADB  
   ```
   adb install -r app-debug.apk
   ```

---

# 📈 Roadmap

- v1.1 fade entre mídias  
- v1.2 overlay HTML  
- v1.3 registro automático de player  
- v2.0 suporte Linux + Web player  

---

# 💜 Desenvolvido por  
**Christian Simon + GPTzílldo**
 Videobes Multimídia (2025)

