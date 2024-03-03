package com.linhnv.springbatchexample.batch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
public abstract class BatchJobService {

    private static final String JOB_NAME = "batchId:%s";

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;

    /**
     * Create new job
     *
     * @param jobBuilder
     * @param stepBuilder
     * @return
     */
    protected abstract Job createJob(JobBuilder jobBuilder, StepBuilder stepBuilder) throws Exception;

    protected String getStepName() {
        return "STEP-01";
    }

    protected abstract void beforeJob(JobExecution jobExecution);

    protected abstract void afterJob(JobExecution jobExecution);

    protected abstract void beforeStep(StepExecution stepExecution);

    protected abstract void afterStep(StepExecution stepExecution);

    /**
     * Execute batch job with batchJobId
     *
     * @param batchJobId
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void execute(String batchJobId) {

        var jobName = String.format(JOB_NAME, batchJobId);

        var jobParameters = new JobParametersBuilder()
                .addString("batchJobId", batchJobId)
                .addString("startTime", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();


        var jobBuilder = new JobBuilder(jobName, jobRepository)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        BatchJobService.this.beforeJob(jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        BatchJobService.this.afterJob(jobExecution);
                    }
                });

        var stepBuilder = new StepBuilder(getStepName(), jobRepository)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        BatchJobService.this.beforeStep(stepExecution);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        BatchJobService.this.afterStep(stepExecution);
                        return null;
                    }
                });

        try {
            jobLauncher.run(createJob(jobBuilder, stepBuilder), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Execute batch job failed: ", e);
        } catch (Exception e) {
            log.error("Error execute batch job: ", e);
        }
    }
}
