<h1 align="center">🏋️ LS Trainer</h1>

<p align="center">
  <em>Sistema de Gestão de Fichas de Treino</em>
</p>
<div align="center">

</div>

---

<p align="center">
  O <strong>LS Trainer</strong> é uma aplicação Full-Stack desenvolvida como meu primeiro projeto freelance. <br>
  O objetivo foi substituir as fichas de treino de papel da academia por uma solução digital segura, responsiva e acessível de qualquer dispositivo.
</p>

### 🌐 Acesso ao Projeto
- 🔗 **Sistema em Produção:** [https://ls-trainer.vercel.app](https://ls-trainer.vercel.app)
- 📚 **Documentação da API (Swagger):** [https://lstrainer-api.onrender.com/swagger-ui.html](https://lstrainer-api.onrender.com/swagger-ui.html)

---

## 🚀 Funcionalidades

- 🔐 **Autenticação Segura:** Login e cadastro com JWT em cookies `HttpOnly`.
- 👥 **Gestão de Alunos:** CRUD completo com isolamento de dados por professor (prevenção de IDOR).
- 🏋️ **Exercícios e Fichas:** Biblioteca de exercícios e montagem dinâmica de treinos (séries, repetições, cargas).
- 📏 **Avaliações e Cardio:** Histórico de medidas e prescrições cardiovasculares.
- 📊 **Dashboard:** Painel com contadores reais e lista de reavaliações próximas.

---

## 🛠️ Stack Tecnológica

<div align="center">

| **Back-end** | **Front-end** | **Database** | **DevOps** |
|:---:|:---:|:---:|:---:|
| ![Java](https://skillicons.dev/icons?i=java) ![Spring](https://skillicons.dev/icons?i=spring) | ![React](https://skillicons.dev/icons?i=react) ![TypeScript](https://skillicons.dev/icons?i=typescript) ![CSS](https://skillicons.dev/icons?i=css) ![Vite](https://skillicons.dev/icons?i=vite) | ![PostgreSQL](https://skillicons.dev/icons?i=postgresql) | ![Docker](https://skillicons.dev/icons?i=docker) ![GitHub Actions](https://skillicons.dev/icons?i=githubactions) |

</div>

---

## 🏗️ Arquitetura da Solução

<p align="center">
  <img src="https://img.shields.io/badge/Vercel-Frontend-black?style=flat-square&logo=vercel" />
  <br>⬇️
  <br>
  <img src="https://img.shields.io/badge/Render-Backend-black?style=flat-square&logo=render" />
  <br>⬇️
  <br>
  <img src="https://img.shields.io/badge/Neon-PostgreSQL-black?style=flat-square&logo=postgresql" />
</p>

O sistema foi arquitetado de forma distribuída:
- **Vercel:** Hospeda o frontend React (SPA).
- **Render:** Hospeda a API Spring Boot (conteinerizada com Docker).
- **Neon.tech:** Banco de dados PostgreSQL Serverless.
- A comunicação entre Vercel e Render é protegida por **CORS** e **Cookies SameSite=None**, garantindo autenticação segura entre domínios diferentes.

---

## ⚙️ Como Rodar Localmente

**Pré-requisitos:** Java 21, Node.js 22+, Docker e Docker Compose.

```bash
# 1. Clonar o repositório
git clone https://github.com/BeSinhorelli/Freelance-LS_Trainer.git
cd Freelance-LS_Trainer

# 2. Subir o Backend e o Banco de Dados com Docker
docker-compose up --build -d

# 3. Rodar o Frontend
cd lstrainer-web
npm install
npm run dev
