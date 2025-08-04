package com.IgorAlan.jobportal.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "job_posts_idx") // Nome do índice onde os documentos serão salvos
public class JobPostDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "jobTitle")
    private String jobTitle;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Keyword, name = "companyName")
    private String companyName;

    @Field(type = FieldType.Keyword, name = "city")
    private String city;

    @Field(type = FieldType.Keyword, name = "state")
    private String state;

    @Field(type = FieldType.Keyword, name = "jobType")
    private String jobType;

    @Field(type = FieldType.Keyword, name = "remote")
    private String remote;

    @Field(type = FieldType.Date, name = "postedDate")
    private LocalDateTime postedDate;
}
