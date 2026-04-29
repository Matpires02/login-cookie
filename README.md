# 🔐 Login Cookie API (Spring Boot)

API de autenticação segura utilizando **Spring Boot 4**, com:

* ✅ Login com **JWT em cookie**
* ✅ Refresh Token
* ✅ Proteção contra **Replay Attack**
* ✅ **Rate Limit inteligente (progressivo)**
* ✅ Segurança com Spring Security
* ✅ Testes automatizados
* ✅ Banco H2 para desenvolvimento

---

# 🚀 Tecnologias

* Java 17
* Spring Boot 4
* Spring Security
* Spring Data JPA
* H2 Database
* JWT (jjwt)
* MapStruct
* JUnit + MockMvc

---

# 📦 Funcionalidades

## 🔑 Autenticação

* Login com email e senha
* Registro de usuário
* Geração de:

  * Access Token (15 min)
  * Refresh Token (7 dias)

## 🍪 Cookies seguros

* Tokens armazenados em cookies HTTP Only
* Proteção contra XSS

## 🛡️ Segurança

### ✔️ Replay Attack Protection

* Cada token possui um `jti`
* Tokens usados são invalidados (blacklist)

### ✔️ Rate Limit inteligente

* Baseado em:

  * IP
* Sistema progressivo:

  * Quanto mais erros → maior bloqueio
* Reset automático no login com sucesso

---

# 📁 Estrutura do Projeto

```
src/
├── controller/
├── service/
├── security/
├── config/
├── entity/
├── repository/
└── audit/
```

---

# ⚙️ Configuração

## application.yml

```yaml
jwt:
  secret: sua_chave_super_secreta_com_no_minimo_32_chars

spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: update
```

---

# 🔌 Endpoints

## 📌 Auth

### ➕ Registrar usuário

```
POST /auth/register
```

Body:

```json
{
  "email": "user@email.com",
  "password": "123456"
}
```

---

### 🔑 Login

```
POST /auth/login
```

Resposta:

* Cookie `access_token`
* Cookie `refresh_token`

---

### 🔄 Refresh Token

```
POST /auth/refresh
```

---

### 🚪 Logout

```
POST /auth/logout
```

---

# 🧪 Testes

## Rodar testes

```bash
mvn test
```

## Tipos de teste implementados

### ✔️ AuthControllerTest

* Registro de usuário
* Login com sucesso
* Login inválido

### ✔️ JwtServiceTest

* Geração de token
* Validação
* Extração de dados

### ✔️ RateLimitTest

* Bloqueio após múltiplas tentativas
* Proteção contra brute force

### ✔️ SecurityTest

* Acesso negado sem token
* Acesso permitido com token válido

---

# 🧠 Como funciona o Rate Limit

* Cada tentativa de login gera consumo de tokens
* Falhas aumentam penalidade
* Sistema usa:

  ```
  chave = IP
  ```

Exemplo:

```
192.168.0.1
```

---

# 🔐 Como funciona o JWT

### Claims:

* `sub` → email
* `roles` → permissões
* `jti` → ID único (anti replay)

---

# ⚠️ Problemas comuns resolvidos

### ❌ Erro JWT (ClassNotFound)

✔️ Solução: alinhar versões do jjwt

### ❌ Forbidden em /auth

✔️ Solução: liberar rota no SecurityConfig e no filtro

### ❌ H2 erro com tabela "user"

✔️ Solução: renomear tabela ou usar escape (`users`)

---

# 📈 Melhorias implementadas

* ✅ Rate limit progressivo
* ✅ Reset do bucket após sucesso
* ✅ Validação por IP
* ✅ JWT sem consulta ao banco (roles no token)
* ✅ Filtro resiliente (não quebra aplicação)

---

# 🚀 Próximos passos (opcional)

* 🔄 Refresh token com rotação
* 🧱 Cache distribuído (Redis)
* 📊 Observabilidade (logs + métricas)
* 🐳 Docker
* ⚙️ CI/CD (GitHub Actions)

---

# 👨‍💻 Autor

Projeto desenvolvido para estudo avançado de:

* Segurança em APIs
* Autenticação moderna
* Proteção contra ataques

---

# 📄 Licença

Uso educacional / livre
