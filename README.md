# java-job-portal

## Descrição
O **Job Portal** é um sistema desenvolvido em Java com o framework Springboot para facilitar a conexão entre candidatos e recrutadores. Ele permite que recrutadores publiquem vagas de emprego e que candidatos se candidatem a essas vagas.

## Funcionalidades
- Cadastro e login de candidatos e recrutadores
- Publicação de vagas de emprego por recrutadores
- Candidatura de candidatos a vagas publicadas
- Listagem de vagas disponíveis pelo candidato
- Candidatos pode salvar vagas de interesse
- Recrutadores podem baixar currículos de candidatos que aplicaram para a vaga que o recrutador anunciou

## Tecnologias Utilizadas
- **Linguagem**: Java
- **Banco de Dados**: MySQL
- **Frameworks**: Spring Boot (Web, Data JPA, Security, Validation, Thymeleaf), Hibernate
- **Ferramentas**: Spring Boot DevTools, WebJars, WebJars Locator, intellij, Docker

## Como Executar o Projeto
1. Clone este repositório:
   ```bash
   git clone https://github.com/IgorAlanAlbuquerque/java-job-portal.git
   ```
2. Navegue até o diretório do projeto:
   ```bash
   cd java-job-portal
   ```
3. Configure o banco de dados:
   ```bash
   docker-compose build .
   docker-compose up -d
   ```
4. Compile e execute o projeto:
   ```bash
   mvn spring-boot:run
   ```
5. Acesse a aplicação no navegador: `http://localhost:8080`

