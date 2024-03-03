package com.linhnv.springbatchexample.service;

import com.linhnv.springbatchexample.batch.SimpleTaskletBatchJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Service
@Slf4j
public class DemoTaskletService extends SimpleTaskletBatchJobService {

    protected DemoTaskletService(
            JobLauncher jobLauncher,
            PlatformTransactionManager transactionManager,
            JobRepository jobRepository,
            ApplicationContext applicationContext) {
        super(jobLauncher, transactionManager, jobRepository, applicationContext);
    }

    @Override
    protected boolean execute() {

        // handle business here

        return false;
    }

    @Override
    protected void beforeJob(JobExecution jobExecution) {
        log.info("===Before job: Job Id = {}, start = {}", jobExecution.getJobId(), jobExecution.getStartTime());
    }

    @Override
    protected void afterJob(JobExecution jobExecution) {
        log.info("===After job: Job Id = {}, end = {}", jobExecution.getJobId(), jobExecution.getEndTime());
    }

    @Override
    protected void beforeStep(StepExecution stepExecution) {
        log.info("===Before step: start = {}", stepExecution.getStartTime());
    }

    @Override
    protected void afterStep(StepExecution stepExecution) {
        log.info("===After step: end = {}", stepExecution.getEndTime());
    }
}
