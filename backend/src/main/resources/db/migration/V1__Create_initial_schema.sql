CREATE TABLE users_type (
    user_type_id BIGSERIAL PRIMARY KEY,
    user_type_name VARCHAR(255) NOT NULL
);

INSERT INTO users_type (user_type_name) VALUES ('Recruiter'), ('JobSeeker');

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN,
    registration_date TIMESTAMP,
    user_type_id BIGINT,
    CONSTRAINT fk_users_type FOREIGN KEY (user_type_id) REFERENCES users_type(user_type_id)
);

CREATE TABLE job_company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    logo VARCHAR(255)
);

CREATE TABLE job_location (
    id BIGSERIAL PRIMARY KEY,
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE job_seeker_profile (
    user_account_id BIGINT PRIMARY KEY,
    work_authorization VARCHAR(255),
    employment_type VARCHAR(255),
    resume VARCHAR(255),
    profile_photo VARCHAR(255),
    CONSTRAINT fk_job_seeker_user FOREIGN KEY (user_account_id) REFERENCES users(user_id)
);

CREATE TABLE recruiter_profile (
    user_account_id BIGINT PRIMARY KEY,
    company VARCHAR(255),
    profile_photo VARCHAR(64),
    CONSTRAINT fk_recruiter_user FOREIGN KEY (user_account_id) REFERENCES users(user_id)
);

CREATE TABLE job (
    job_post_id BIGSERIAL PRIMARY KEY,
    job_title VARCHAR(255) NOT NULL,
    description_of_job TEXT,
    active BOOLEAN,
    job_type VARCHAR(255),
    salary VARCHAR(255),
    remote VARCHAR(255),
    posted_date TIMESTAMP,
    posted_by_id BIGINT,
    job_company_id BIGINT,
    job_location_id BIGINT,
    CONSTRAINT fk_job_post_user FOREIGN KEY (posted_by_id) REFERENCES users(user_id),
    CONSTRAINT fk_job_post_company FOREIGN KEY (job_company_id) REFERENCES job_company(id),
    CONSTRAINT fk_job_post_location FOREIGN KEY (job_location_id) REFERENCES job_location(id)
);

CREATE TABLE job_seeker_save (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    CONSTRAINT fk_save_seeker FOREIGN KEY (profile_id) REFERENCES job_seeker_profile(user_account_id),
    CONSTRAINT fk_save_job FOREIGN KEY (job_id) REFERENCES job(job_post_id),
    UNIQUE (profile_id, job_id)
);

CREATE TABLE job_seeker_apply (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    apply_date TIMESTAMP,
    cover_letter TEXT,
    CONSTRAINT fk_apply_seeker FOREIGN KEY (profile_id) REFERENCES job_seeker_profile(user_account_id),
    CONSTRAINT fk_apply_job FOREIGN KEY (job_id) REFERENCES job(job_post_id),
    UNIQUE (profile_id, job_id)
);

CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    job_seeker_profile_id BIGINT,
    name VARCHAR(255),
    experience_level VARCHAR(255),
    years_of_experience VARCHAR(255),
    CONSTRAINT fk_skills_seeker FOREIGN KEY (job_seeker_profile_id) REFERENCES job_seeker_profile(user_account_id)
);