package i.code.java.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchResource {

    private static final Logger log = LoggerFactory.getLogger(BatchResource.class);

    private JobRegistry jobRegistry;
    private JobLauncher jobLauncher;

    @Autowired
    public BatchResource(final JobRegistry jobRegistry,
            JobLauncher jobLauncher) {
        this.jobRegistry = jobRegistry;
        this.jobLauncher = jobLauncher;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void triggerBatch(@RequestBody String filename) throws Exception {
        log.info("Triggering batch job for: {}", filename);

        JobExecution execution = jobLauncher.run(jobRegistry.getJob("uppercase"),
                new JobParametersBuilder().addString("filename", filename)
                        .toJobParameters());

        log.info("Started job: {}", execution);
    }
}
