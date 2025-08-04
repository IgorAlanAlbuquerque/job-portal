package com.IgorAlan.jobportal.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.IgorAlan.jobportal.elasticsearch.document.JobPostDocument;

@Repository
public interface JobSearchRepository extends ElasticsearchRepository<JobPostDocument, Long> {

    Page<JobPostDocument> findByJobTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);

    Page<JobPostDocument> findByJobTitleContainsOrDescriptionContainsAndCityAndJobTypeIn(
            String keyword, String city, List<String> jobTypes, Pageable pageable
    );
}
