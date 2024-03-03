package com.linhnv.springbatchexample.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
public abstract class SimpleTaskletBatchJobService extends BatchJobService {

    private final PlatformTransactionManager transactionManager;
    private final ApplicationContext applicationContext;

    protected SimpleTaskletBatchJobService(
            JobLauncher jobLauncher,
            PlatformTransactionManager transactionManager,
            JobRepository jobRepository,
            ApplicationContext applicationContext) {
        super(jobLauncher, jobRepository);
        this.transactionManager = transactionManager;
        this.applicationContext = applicationContext;
    }


    @Override
    protected Job createJob(JobBuilder jobBuilder, StepBuilder stepBuilder) {

        Step step = stepBuilder.tasklet(applicationContext.getBean(SimpleTaskletBatchExecutor.class).caller(this), transactionManager)
                .build();

        return jobBuilder.start(step)
                .build();
    }

    /**
     * Execute tasklet job
     *
     * @return true if success, other fail
     */
    protected abstract boolean execute();

    @Component
    @Scope("prototype")
    public static class SimpleTaskletBatchExecutor implements Tasklet {

        private SimpleTaskletBatchJobService caller;

        public Tasklet caller(SimpleTaskletBatchJobService simpleTaskletBatchJobService) {
            this.caller = simpleTaskletBatchJobService;
            return this;
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

            boolean result = caller != null && caller.execute();

            contribution.setExitStatus(result ? ExitStatus.COMPLETED : ExitStatus.FAILED);

            return RepeatStatus.FINISHED;
        }
    }

}
