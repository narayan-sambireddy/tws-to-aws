package narayan.awsonboarding.twstoaws;

import java.util.Date;
import java.util.function.Function;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;

/**
 * 
 * @author narayana
 *
 */
@Component("accountBatchHandler")
public class AccountBatchHandler implements Function<S3Event, String> {

	private final JobLauncher jobLauncher;
	private final Job eodJob;

	public AccountBatchHandler(JobLauncher jobLauncher, Job eodJob) {
		this.jobLauncher = jobLauncher;
		this.eodJob = eodJob;
	}

	@Override
	public String apply(S3Event event) {
		S3Entity s3 = event.getRecords().get(0).getS3();
		String bucketName = s3.getBucket().getName();
		String objectKkey = s3.getObject().getKey();
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("bucketName", bucketName)
				.addString("objectKey", objectKkey)
				.addDate("RUN-DATE", new Date()).toJobParameters();
		
		try {
			jobLauncher.run(eodJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "JOB COMPLETED!!";
	}
}
