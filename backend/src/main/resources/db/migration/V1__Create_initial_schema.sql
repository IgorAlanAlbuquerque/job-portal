-- Tabela de Tipos de Usuário
CREATE TABLE users_type (
    user_type_id SERIAL PRIMARY KEY,
    user_type_name VARCHAR(255)
);

-- Dados iniciais para tipos de usuário
INSERT INTO users_type (user_type_name) VALUES ('Recruiter'), ('Job Seeker');

-- Tabela de Usuários
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    is_active BOOLEAN,
    registration_date TIMESTAMP,
    user_type_id INT,
    CONSTRAINT fk_users_type FOREIGN KEY (user_type_id) REFERENCES users_type(user_type_id)
);

-- Tabela de Empresas
CREATE TABLE job_company (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    logo VARCHAR(255)
);

-- Tabela de Localizações
CREATE TABLE job_location (
    id SERIAL PRIMARY KEY,
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255)
);

-- Tabela de Perfil do Candidato (Job Seeker)
CREATE TABLE job_seeker_profile (
    user_account_id INT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    employment_type VARCHAR(255),
    profile_photo VARCHAR(255),
    resume VARCHAR(255),
    work_authorization VARCHAR(255),
    CONSTRAINT fk_job_seeker_user FOREIGN KEY (user_account_id) REFERENCES users(user_id)
);

-- Tabela de Perfil do Recrutador
CREATE TABLE recruiter_profile (
    user_account_id INT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    company VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    profile_photo VARCHAR(64),
    CONSTRAINT fk_recruiter_user FOREIGN KEY (user_account_id) REFERENCES users(user_id)
);

-- Tabela de Vagas
CREATE TABLE job_post_activity (
    job_post_id SERIAL PRIMARY KEY,
    posted_by_id INT,
    job_company_id INT,
    job_location_id INT,
    job_title VARCHAR(255),
    description_of_job TEXT, -- Usar TEXT para descrições longas
    job_type VARCHAR(255),
    salary VARCHAR(255),
    remote VARCHAR(255),
    posted_date TIMESTAMP,
    CONSTRAINT fk_job_post_user FOREIGN KEY (posted_by_id) REFERENCES users(user_id),
    CONSTRAINT fk_job_post_company FOREIGN KEY (job_company_id) REFERENCES job_company(id),
    CONSTRAINT fk_job_post_location FOREIGN KEY (job_location_id) REFERENCES job_location(id)
);

-- Tabela de Vagas Salvas pelo Candidato
CREATE TABLE job_seeker_save (
    id SERIAL PRIMARY KEY,
    user_id INT,
    job_post_id INT,
    CONSTRAINT fk_save_seeker FOREIGN KEY (user_id) REFERENCES job_seeker_profile(user_account_id),
    CONSTRAINT fk_save_job FOREIGN KEY (job_post_id) REFERENCES job_post_activity(job_post_id),
    UNIQUE (user_id, job_post_id)
);

-- Tabela de Aplicações do Candidato
CREATE TABLE job_seeker_apply (
    id SERIAL PRIMARY KEY,
    user_id INT,
    job_post_id INT,
    apply_date TIMESTAMP,
    cover_letter TEXT,
    CONSTRAINT fk_apply_seeker FOREIGN KEY (user_id) REFERENCES job_seeker_profile(user_account_id),
    CONSTRAINT fk_apply_job FOREIGN KEY (job_post_id) REFERENCES job_post_activity(job_post_id),
    UNIQUE (user_id, job_post_id)
);

-- Tabela de Habilidades (Skills)
CREATE TABLE skills (
    id SERIAL PRIMARY KEY,
    job_seeker_profile_id INT,
    name VARCHAR(255),
    experience_level VARCHAR(255),
    years_of_experience VARCHAR(255),
    CONSTRAINT fk_skills_seeker FOREIGN KEY (job_seeker_profile_id) REFERENCES job_seeker_profile(user_account_id)
);