package com.linhnv.springbatchexample.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
public abstract class SimpleChunkBatchJobService<T> extends BatchJobService {

    private final PlatformTransactionManager transactionManager;

    private final int DEFAULT_CHUNK_SIZE = 1000;

    public SimpleChunkBatchJobService(JobLauncher jobLauncher, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        super(jobLauncher, jobRepository);
        this.transactionManager = transactionManager;
    }

    protected abstract ItemReader<T> read() throws Exception;

    protected abstract T process(Object entity) throws Exception;

    protected abstract void write(Chunk<?> chunk) throws Exception;

    protected int getMaxChunkSize() {
        return DEFAULT_CHUNK_SIZE;
    }

    @Override
    protected Job createJob(JobBuilder jobBuilder, StepBuilder stepBuilder) throws Exception {
        Step step = stepBuilder.chunk(getMaxChunkSize(), transactionManager)
                .reader(read())
                .processor(this::process)
                .writer(this::write)
                .build();

        return jobBuilder.start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

}
