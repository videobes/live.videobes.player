# -LiveVideobes-Player-Android-
O LiveVideobes Player é um aplicativo Android desenvolvido para transformar qualquer TV Box, tablet Android, ou TV Android em um painel profissional de mídia indoor, totalmente integrado ao painel LiveVideobes orquestrado via n8n.
Ele foi projetado para:

Rodar em modo kiosk (bloqueando o sistema operacional).

Exibir vídeo de intro personalizado ao iniciar.

Tocar playlists locais (vídeos e imagens).

Sincronizar horário e clima.

Atualizar conteúdos automaticamente pelo painel.

Permitir configuração apenas com menu secreto (3× voltar ou Ctrl+Z).

Este repositório contém o código-fonte completo, pronto para compilar no Android Studio.

🚀 Funcionalidades principais
✔ Intro personalizada

Ao ligar o dispositivo, o player exibe 10 segundos do vídeo oficial da Videobes (localizado em res/raw/) e segue automaticamente para o conteúdo do cliente.

✔ Modo Kiosk Profundo

Tira barra de navegação

Tira barra de status

Bloqueia HOME e BACK

Repõe o player quando o Android tenta abrir outro app

Pode substituir o launcher original (onde permitido)

✔ Tela de configuração minimalista

Aparece somente:

Na primeira inicialização, OU

Ao acionar o menu secreto:

3× Voltar

Ou Ctrl+Z no teclado

Configura:

Wi-Fi

Pasta de mídia interna

(Futuro) Código do Canal / ID do Player

Menu admin (trocar pasta, pausar, sair, etc.)

✔ Loop de mídia inteligente

Carrega vídeos/imagens da pasta escolhida

Shuffle automático

Detecção automática de formatos

Suporte a MP4, MKV, MOV, JPG, PNG

Loop ininterrupto, mesmo offline

✔ Sincronização remota (API + n8n)

O painel envia:

Playlist JSON

Alterações de canal

Horários de exibição

Announces

Overlays

O player consulta periodicamente (configurável).

✔ Hora e clima (v1 incluído)

Player chama:

http://worldtimeapi.org/api/timezone/...

https://api.openweathermap.org/...

Formato JSON, já suportado na v1.
O painel pode habilitar overlay ou slide automático.

✔ Compatível com qualquer Android

Funciona em:

TV Boxes genéricos

Tablets antigos

TVs Android (Aiwa, TCL, Philco etc.)

Android 9.0+
</br>
🧱 ```text
                      Arquitetura (Visão Geral)

           ┌─────────────────────────────────────────────────┐
           │               Painel LiveVideobes               │
           │        API + Orquestração via n8n               │
           └───────────────────────┬─────────────────────────┘
                                   │
                                   │ REST (JSON)
                                   ▼
       ┌─────────────────────────────────────────────────────────┐
       │                  LiveVideobes Player (Android)          │
       │---------------------------------------------------------│
       │ • Roda vídeo de intro                                   │
       │ • Gerencia playlists                                    │
       │ • Faz cache local (offline)                             │
       │ • Recebe triggers automáticos (via n8n/API)             │
       │ • Exibe clima e hora (v1)                               │
       │ • Atualização remota de playlist                        │
       └─────────────────────────────────────────────────────────┘
```

🔌 Endpoints Oficiais (v1.0)

Estes endpoints devem existir no painel live.videobes.com:

🔹 1. Obter playlist do player
GET /api/player/{playerId}/playlist

Exemplo de resposta:
{
  "player_id": "alkuwait-entrada01",
  "playlist_version": 7,
  "poll_interval": 30,
  "timezone": "America/Sao_Paulo",
  "weather_city": "Rio de Janeiro",
  "items": [
    {
      "id": "intro01",
      "type": "video",
      "url": "https://cdn.videobes.com/alkuwait/cardapio01.mp4"
    },
    {
      "id": "bannerTemp",
      "type": "weather",
      "layout": "horizontal-light"
    },
    {
      "id": "imagem01",
      "type": "image",
      "url": "https://cdn.videobes.com/alkuwait/banner1.png",
      "duration_ms": 10000
    }
  ]
}

🔹 2. Registrar novo player
POST /api/player/register

Corpo:
{
  "serial": "GS25-AA11-BC77",
  "model": "MXQ Pro",
  "version": "1.0",
  "mac_wifi": "44:12:AB:08:17:F1"
}

🔹 3. Enviar status (opcional)
POST /api/player/{id}/status

Exemplos de status enviados:
{
  "current_media": "cardapio01.mp4",
  "uptime_minutes": 551,
  "last_sync": "2025-10-11T02:11:30Z",
  "disk_free": "8.5 GB",
  "playlist_version": 7
}

🛰 Integração com n8n

No n8n, você terá workflows como:

Upload de mídia 👉 Atualiza playlist JSON 👉 Notifica player

Quando cliente troca vídeo 👉 Dispara PATCH para API

Rotina de limpeza automática no CDN

Gerenciamento de canais

Logs de players

E, o melhor:

👉 Não existe mais rendering no servidor
O player usa mídia local, só recebe instruções.

🕒 Hora e Clima – Implementação (v1 já incluso)

O player consulta:

Hora:
GET http://worldtimeapi.org/api/timezone/America/Sao_Paulo

Clima:
GET https://api.openweathermap.org/data/2.5/weather?q=Rio de Janeiro&appid=API_KEY


O painel envia no JSON tipo:

{
  "type": "weather",
  "city": "Rio de Janeiro",
  "layout": "clean"
}


O player exibe como:

Overlay sobre vídeo

Slide dedicado

Widget minimalista

📁 Estrutura de diretórios (resumo)
app/</br>
 ├─ src/</br>
 │   ├─ main/</br>
 │   │   ├─ java/com/videobes/liveplayer/</br>
 │   │   │    ├─ PlayerActivity.kt</br>
 │   │   │    ├─ BootReceiver.kt</br>
 │   │   │    ├─ KioskHelper.kt</br>
 │   │   │    ├─ Prefs.kt</br>
 │   │   │    ├─ MediaScanner.kt</br>
 │   │   │    └─ WeatherTimeHelper.kt</br>
 │   │   ├─ res/</br>
 │   │   │   ├─ raw/live_videobes_intro.mp4</br>
 │   │   │   ├─ layout/activity_player.xml</br>
 │   │   │   └─ drawable/icons...</br>

🛠 Como compilar

Instale Android Studio Iguana

Clone o repositório:

git clone https://github.com/videobes/livevideobes-player.git


Abra no Android Studio

Vá em Build > Make Project

Conecte um TV Box via USB ou ADB

Instale:

adb install -r app-debug.apk

🧪 Testes recomendados

Ligar/desligar o dispositivo

Trocar pasta via admin secreto

Desconectar da internet

Mudar horário do sistema

Simular queda de energia

Testar em TV Android (Aiwa/TCL)

Testar em tablet antigo

📈 Roadmap
v1.1

Overlay elegante de clima e relógio

Fade entre mídia e próxima mídia

Cache mais inteligente

v1.2

Login com Código do Canal

Registro automático no painel

Triggers de sincronização imediata

v1.3

Suporte a HTML5 overlay

Notícias / RSS / WordPress posts

v2.0

Player multiplataforma (Linux, HTML, iOS)

Atualizações remotas completas

Monitoramento total

Kiosk ultra profundo (Device Owner)

❤️ Autor

Videobes Multimídia + GPTzílldo
2025 – Rio de Janeiro

“Se é pra dominar o Android, a gente domina com estilo.”
