# 📊 Painel de Criptoativos

Aplicação desktop desenvolvida em **Java 17** para acompanhamento de cotações de criptoativos. O sistema consulta dados de mercado através das APIs da **Mercado Bitcoin** e **Binance**, apresenta as cotações em uma interface gráfica e permite manter uma lista personalizada de moedas para acompanhamento.

## ✨ Funcionalidades

- 📈 Consulta de cotações de criptoativos
- 💰 Exibição de valores em **Real (BRL)** e **Dólar (USD)**
- 🔄 Atualização automática das cotações a cada **60 segundos**
- ➕ Adição de criptomoedas ao painel
- ➖ Remoção de criptomoedas monitoradas
- 🔎 Validação de moedas nos mercados suportados
- 📋 Persistência local da lista de moedas acompanhadas
- 🖱️ Visualização de detalhes da moeda por duplo clique
- 🌙 Alternância entre modo claro e modo escuro
- 💾 Persistência da preferência de tema

## 🖥️ Interface

A tabela principal apresenta:

| Campo | Descrição |
|---|---|
| **Moeda** | Símbolo do criptoativo |
| **Compra em real** | Cotação de compra em BRL |
| **Venda em real** | Cotação de venda em BRL |
| **Compra em dólar** | Cotação de compra em USD |
| **Venda em dólar** | Cotação de venda em USD |

## 🛠️ Tecnologias

| Tecnologia | Utilização |
|---|---|
| **Java 17 LTS** | Linguagem e plataforma principal |
| **Swing** | Interface gráfica desktop |
| **FlatLaf** | Temas e aparência da interface |
| **Apache Maven** | Gerenciamento de dependências e build |
| **org.json** | Processamento das respostas JSON |
| **Mercado Bitcoin API** | Consulta de cotações |
| **Binance API** | Consulta de pares e cotações |
| **Java HTTP Client** | Comunicação com APIs externas |

## 🏗️ Arquitetura

O projeto é organizado por responsabilidades, separando interface, controle, serviços, comunicação HTTP e persistência local.

```text
src/main/java/po23s/
├── common/       # Repositórios, configurações e utilitários
├── controller/   # Coordenação das ações da aplicação
├── helper/       # Classes auxiliares
├── http/         # Comunicação HTTP
├── service/      # Regras e serviços de negócio
├── updater/      # Atualização dos dados da tabela
└── view/         # Interface gráfica Swing
```

### Principais componentes

- **View:** construção da interface e interação visual.
- **Controller:** coordenação das ações do usuário e atualização da interface.
- **Service:** validação, consulta e processamento dos dados de criptoativos.
- **HTTP:** comunicação com serviços externos.
- **Repository/Common:** persistência local e utilitários.

## 🌐 APIs utilizadas

### Mercado Bitcoin

Consulta de cotações através do endpoint:

```text
https://www.mercadobitcoin.net/api/{MOEDA}/ticker
```

### Binance

O projeto utiliza endpoints da Binance para identificação de pares e consulta de informações de mercado:

```text
https://api.binance.com/api/v3/exchangeInfo
https://api.binance.com/api/v3/ticker/bookTicker
https://api.binance.com/api/v3/ticker/24hr
```

É necessária uma conexão com a Internet para obter as cotações atualizadas.

## 📋 Requisitos

- **JDK 17 ou superior**
- **Apache Maven**
- Conexão com a Internet

### Instalação

**JDK 17 — Eclipse Adoptium:**  
https://adoptium.net/temurin/releases/?version=17

**Apache Maven:**  
https://maven.apache.org/download.cgi

**Visual Studio Code:**  
https://code.visualstudio.com/download

Para o VS Code, recomenda-se o **Extension Pack for Java**.

## 🚀 Como executar

Clone o repositório:

```bash
git clone https://github.com/JacanaFSilva/Painel-de-Criptoativos.git
cd Painel-de-Criptoativos
```

Compile e empacote a aplicação:

```bash
mvn clean package
```

Execute o JAR gerado:

```bash
java -jar target/cch-cripto-1.0-SNAPSHOT.jar
```

No Windows PowerShell:

```powershell
java -jar target\cch-cripto-1.0-SNAPSHOT.jar
```

Também é possível executar diretamente pelo Maven:

```bash
mvn exec:java
```

## 📦 Executável

O projeto também disponibiliza um executável da aplicação:

```text
CryptoDashboard.exe
```

Além disso, o processo de build gera um JAR executável com as dependências empacotadas.

## ⚙️ Persistência local

A aplicação utiliza arquivos locais para preservar informações entre execuções.

### `config.properties`

Armazena preferências da aplicação, incluindo o estado do modo escuro:

```properties
modoEscuro=true
```

### `moedas.txt`

Armazena a lista de criptoativos adicionados ao painel.

## 🧭 Como utilizar

1. Inicie a aplicação.
2. Digite o símbolo da moeda desejada.
3. Clique em **Adicionar**.
4. A moeda será adicionada caso seja identificada como válida.
5. Selecione uma moeda e clique em **Remover** para retirá-la do painel.
6. Dê **duplo clique** sobre uma moeda para consultar seus detalhes.
7. Utilize **Modo Escuro** para alternar o tema da aplicação.

Exemplos:

```text
BTC
ETH
LTC
XRP
```

A disponibilidade de cada ativo depende dos mercados consultados.

## 🔄 Atualização dos dados

As cotações são atualizadas automaticamente em um intervalo de **60 segundos**.

Portanto, os valores exibidos correspondem aos dados retornados pelas APIs na última atualização realizada, não constituindo uma garantia de cotação instantânea.

## 📁 Estrutura do projeto

```text
Painel-de-Criptoativos/
├── src/
│   └── main/
│       ├── java/
│       │   └── po23s/
│       └── resources/
├── config.properties
├── moedas.txt
├── CryptoDashboard.exe
├── pom.xml
├── LICENSE
└── README.md
```

> O diretório `target/` é gerado automaticamente pelo Maven durante o build.

## 🧪 Build

Limpar os artefatos anteriores e gerar uma nova versão:

```bash
mvn clean package
```

Apenas compilar:

```bash
mvn compile
```

Executar pelo Maven:

```bash
mvn exec:java
```

## ⚠️ Observações

- O aplicativo depende de conexão com a Internet para consultar as APIs.
- As cotações são fornecidas por serviços externos e podem sofrer alterações ou indisponibilidade.
- O projeto **não realiza compra, venda ou transferência de criptomoedas**.
- O sistema possui finalidade de **consulta e acompanhamento de informações de mercado**.
- Os arquivos `config.properties` e `moedas.txt` são utilizados para persistência local.

## 📄 Licença

Este projeto está licenciado sob a **Apache License 2.0**.

Consulte o arquivo [LICENSE](LICENSE) para o texto completo da licença.

## 👨‍💻 Autor

**Jaçanã F. Silva**

Projeto desenvolvido em Java como aplicação desktop para consulta e acompanhamento de criptoativos.
