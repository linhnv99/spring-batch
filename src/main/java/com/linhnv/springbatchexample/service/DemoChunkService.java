package com.linhnv.springbatchexample.service;

import com.linhnv.springbatchexample.batch.SimpleChunkBatchJobService;
import com.linhnv.springbatchexample.dto.PlayerFieldSetMapper;
import com.linhnv.springbatchexample.entity.Player;
import com.linhnv.springbatchexample.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Service
@Slf4j
public class DemoChunkService extends SimpleChunkBatchJobService<Player> {

    @Value("${file.input}")
    private String fileInput;

    private final PlayerRepository playerRepository;

    public DemoChunkService(
            JobLauncher jobLauncher,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PlayerRepository playerRepository) {
        super(jobLauncher, jobRepository, transactionManager);
        this.playerRepository = playerRepository;
    }

    @Override
    protected int getMaxChunkSize() {
        return 2;
    }

    @Override
    protected ItemReader<Player> read() {
        FlatFileItemReader<Player> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource(fileInput));
        DefaultLineMapper<Player> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new PlayerFieldSetMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper);
        itemReader.open(new ExecutionContext());
        itemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        return itemReader;
    }

    @Override
    protected Player process(Object entity) {
        Player player = (Player) entity;
        if (player != null && player.getDebutYear() > 1975) {
            return player;
        }
        return null;
    }

    @Override
    protected void write(Chunk<?> chunk) {
        if (chunk.isEmpty()) {
            return;
        }
        List<Player> players = (List<Player>) chunk.getItems();
        playerRepository.saveAll(players);
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
