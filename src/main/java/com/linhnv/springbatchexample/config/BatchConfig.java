package com.linhnv.springbatchexample.config;

import com.linhnv.springbatchexample.entity.Player;
import com.linhnv.springbatchexample.repository.PlayerRepository;
import com.linhnv.springbatchexample.dto.PlayerFieldSetMapper;
import com.linhnv.springbatchexample.dto.PlayerProcessor;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    // override batch config here
    @Value("${file.input}")
    private String fileInput;

    @Autowired
    private PlayerRepository playerRepository;

    @Bean
    public ItemReader<Player> readCsv() throws Exception {
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

    @Bean
    public PlayerProcessor createItemProcessor() {
        return new PlayerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Player> productWriter() {
        RepositoryItemWriter<Player> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(playerRepository);
        repositoryItemWriter.setMethodName("save");
        return repositoryItemWriter;
    }

    @Bean
    public Step playerStep(JobRepository jobRepository, PlatformTransactionManager transactionManage) throws Exception {
        var step = new StepBuilder("stepProduct", jobRepository)
                .<Player, Player>chunk(2, transactionManage)
                .reader(readCsv())
                .processor(createItemProcessor())
                .writer(productWriter())
                .build();
        return step;
    }

    @Bean
    public Job playerJob(JobRepository jobRepository, @Qualifier("playerStep") Step step1) {
        return new JobBuilder("playerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }
}
