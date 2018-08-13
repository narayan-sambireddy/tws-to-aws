package narayan.awsonboarding.twstoaws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

import com.amazonaws.services.lambda.runtime.events.S3Event;

/**
 * TWS to AWS OnBoarding POC
 * 
 * @author narayana
 *
 */
@SpringBootApplication
public class TwsToAwsApplication extends SpringBootRequestHandler<S3Event, String> {

	public static void main(String[] args) {
		SpringApplication.run(TwsToAwsApplication.class, args);
	}

}
