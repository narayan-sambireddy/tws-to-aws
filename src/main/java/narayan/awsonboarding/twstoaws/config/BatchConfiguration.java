package narayan.awsonboarding.twstoaws.config;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * 
 * @author narayana
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private final JobBuilderFactory jobs;
	private final StepBuilderFactory steps;

	public BatchConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps) {
		this.jobs = jobs;
		this.steps = steps;
	}

	@Bean
	public Job eodJob(Step validateFile, Step processFile) {
		return jobs.get("eodJob").start(validateFile).next(processFile).build();
	}

	@Bean
	@JobScope
	public Step validateFile() {
		return steps.get("validateFile").tasklet((sc, ck) -> {
			System.out.println("Executing.... validateFile step");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Alpha> fileReader(ResourceLoader resourceLoader) {
		return new FlatFileItemReader<Alpha>() {
			{
				setLinesToSkip(1);
				setResource(resourceLoader.getResource("s3://s3-dynamo-demo/alpha.txt"));
				setLineMapper(new DefaultLineMapper<Alpha>() {
					{
						setLineTokenizer(new DelimitedLineTokenizer() {
							{
								setDelimiter("|");
								setNames(new String[] { "id", "name" });
							}
						});
						setFieldSetMapper(new BeanWrapperFieldSetMapper<Alpha>() {
							{
								setTargetType(Alpha.class);
							}
						});
					}
				});
			}
		};
	}

	@Bean
	@StepScope
	public ItemWriter<Alpha> consoleWriter() {
		return new ItemWriter<Alpha>() {
			@Override
			public void write(List<? extends Alpha> items) throws Exception {
				items.forEach(System.out::println);
			}
		};
	}

	@Bean
	@JobScope
	public Step processFile(FlatFileItemReader<Alpha> fileReader, ItemWriter<Alpha> consoleWriter) {
		return steps.get("processFile").<Alpha, Alpha>chunk(2).reader(fileReader).writer(consoleWriter)
				.listener(new ChunkListener() {
					
					@Override
					public void beforeChunk(ChunkContext context) {
						System.out.println("------- before chunk -------");
					}
					
					@Override
					public void afterChunkError(ChunkContext context) {
						System.out.println("------- after chunk error -------");
					}
					
					@Override
					public void afterChunk(ChunkContext context) {
						System.out.println("------- after chunk -------");
					}
				})
				.build();
	}

	@Bean
	public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
	SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher() {
		@Override
		public JobExecution run(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException,
				JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
			jobParameters = new JobParametersBuilder(jobParameters).addDate("RUN-DATE", new Date())
					.toJobParameters();
			return super.run(job, jobParameters);
		}
	};
	simpleJobLauncher.setJobRepository(jobRepository);
	return simpleJobLauncher;
	}

}
