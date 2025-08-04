package com.IgorAlan.jobportal.controller;

import com.IgorAlan.jobportal.models.*;
import com.IgorAlan.jobportal.services.JobPostActivityService;
import com.IgorAlan.jobportal.services.JobSeekerApplyService;
import com.IgorAlan.jobportal.services.JobSeekerSaveService;
import com.IgorAlan.jobportal.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/job-post-activity")
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @GetMapping("/")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String job,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String partTime,
            @RequestParam(required = false) String fullTime,
            @RequestParam(required = false) String freelance,
            @RequestParam(required = false) String remoteOnly,
            @RequestParam(required = false) String officeOnly,
            @RequestParam(required = false) String partialRemote,
            @RequestParam(required = false) boolean today,
            @RequestParam(required = false) boolean days7,
            @RequestParam(required = false) boolean days30) {

        // Lógica para os filtros de tempo
        LocalDate searchDate = null;
        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        }

        // Lógica para os filtros de tipo de trabalho
        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
        }

        // Lógica para os filtros de local de trabalho
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
        }

        // Busca de ofertas de trabalho
        List<JobPostActivity> jobPost = jobPostActivityService.search(job, location,
                Arrays.asList(partTime, fullTime, freelance),
                Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);

        // Lógica de personalização para usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            // Usuário anônimo, retorna as ofertas padrão
            return ResponseEntity.ok(jobPost);
        }

        // Usuário autenticado, ajusta as ofertas com o status de candidatura ou salvamento
        Object currentUserProfile = usersService.getCurrentUserProfile();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
            // Se for recrutador, retorne as vagas específicas para ele
            List<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId());
            return ResponseEntity.ok(recruiterJobs);
        } else {
            // Se for candidato, adiciona o status de candidatura e salvo para cada vaga
            List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
            List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

            for (JobPostActivity jobActivity : jobPost) {
                boolean exist = false;
                boolean saved = false;
                for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                    if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
                        jobActivity.setIsActive(true);
                        exist = true;
                        break;
                    }
                }

                for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                    if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
                        jobActivity.setIsSaved(true);
                        saved = true;
                        break;
                    }
                }

                if (!exist) {
                    jobActivity.setIsActive(false);
                }
                if (!saved) {
                    jobActivity.setIsSaved(false);
                }
            }
            return ResponseEntity.ok(jobPost);
        }
    }

    @GetMapping("/global-search")
    public ResponseEntity<?> globalSearch(
            @RequestParam(required = false) String job,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String partTime,
            @RequestParam(required = false) String fullTime,
            @RequestParam(required = false) String freelance,
            @RequestParam(required = false) String remoteOnly,
            @RequestParam(required = false) String officeOnly,
            @RequestParam(required = false) String partialRemote,
            @RequestParam(required = false, defaultValue = "false") boolean today,
            @RequestParam(required = false, defaultValue = "false") boolean days7,
            @RequestParam(required = false, defaultValue = "false") boolean days30) {

        LocalDate searchDate = null;
        boolean dateSearchFlag = true;
        boolean includeDefaultJobTypes = false;
        boolean includeDefaultRemoteTypes = false;

        // Lógica para data
        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false;
        }

        // Tipos de trabalho
        List<String> jobTypes = new ArrayList<>();
        if (partTime != null) jobTypes.add("Part-Time");
        if (fullTime != null) jobTypes.add("Full-Time");
        if (freelance != null) jobTypes.add("Freelance");
        if (jobTypes.isEmpty()) {
            jobTypes = Arrays.asList("Part-Time", "Full-Time", "Freelance");
            includeDefaultJobTypes = true;
        }

        // Formas de trabalho (remoto/presencial)
        List<String> workModes = new ArrayList<>();
        if (remoteOnly != null) workModes.add("Remote-Only");
        if (officeOnly != null) workModes.add("Office-Only");
        if (partialRemote != null) workModes.add("Partial-Remote");
        if (workModes.isEmpty()) {
            workModes = Arrays.asList("Remote-Only", "Office-Only", "Partial-Remote");
            includeDefaultRemoteTypes = true;
        }

        // Quando não há filtros, retorna tudo
        boolean noSearchCriteria = !dateSearchFlag && includeDefaultJobTypes && includeDefaultRemoteTypes
                && !StringUtils.hasText(job) && !StringUtils.hasText(location);

        List<JobPostActivity> jobPost;
        if (noSearchCriteria) {
            jobPost = jobPostActivityService.getAll();
        } else {
            jobPost = jobPostActivityService.search(job, location, jobTypes, workModes, searchDate);
        }

        return ResponseEntity.ok(jobPost);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNew(@RequestBody JobPostActivity jobPostActivity) {
        User user = usersService.getCurrentUser();
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        JobPostActivity createdJobPost = jobPostActivityService.addNew(jobPostActivity);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdJobPost);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> editJob(@PathVariable Long id) {
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

        if (jobPostActivity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 Not Found
        }

        return ResponseEntity.ok(jobPostActivity);  // 200 OK
    }
}
