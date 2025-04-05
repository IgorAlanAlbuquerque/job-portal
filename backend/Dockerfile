# Usa a imagem oficial do MySQL
FROM mysql:latest

# Define a variável de ambiente para a senha do root
ENV MYSQL_ROOT_PASSWORD=senha123

# Copia o script SQL para o diretório de inicialização do MySQL
COPY init.sql /docker-entrypoint-initdb.d/

# Expõe a porta 3306
EXPOSE 3306