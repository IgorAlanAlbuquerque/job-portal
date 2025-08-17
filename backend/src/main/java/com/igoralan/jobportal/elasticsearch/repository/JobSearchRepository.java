package com.igoralan.jobportal.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.igoralan.jobportal.elasticsearch.document.JobDocument;

@Repository
public interface JobSearchRepository extends ElasticsearchRepository<JobDocument, Long> {

    Page<JobDocument> findByJobTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);

    Page<JobDocument> findByJobTitleContainsOrDescriptionContainsAndCityAndJobTypeIn(
            String keyword, String city, List<String> jobTypes, Pageable pageable);
}
