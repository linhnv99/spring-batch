package com.linhnv.springbatchexample.controller;

import com.linhnv.springbatchexample.service.DemoChunkService;
import com.linhnv.springbatchexample.service.DemoTaskletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
public class DemoController {

    private final Job playerJob;
    private final JobLauncher jobLauncher;

    @GetMapping("/simple")
    public void simple() {
        try {
            JobParameters parameters = new JobParametersBuilder().addLong("Start-At", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(playerJob, parameters);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    private final DemoTaskletService demoTaskletService;
    private final DemoChunkService demoChunkService;

    @GetMapping("/tasklet")
    public void tasklet() {
        demoTaskletService.execute("Tasklet");
    }

    @GetMapping("/chunk")
    public void chunk() {
        demoChunkService.execute("Chunk");
    }

}
