package narayan.awsonboarding.twstoaws;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;
import org.springframework.context.annotation.Bean;

/**
 * TWS to AWS OnBoarding POC
 * 
 * @author narayana
 *
 */
@SpringBootApplication
public class TwsToAwsApplication extends SpringBootRequestHandler<String, String> {

	public static void main(String[] args) {
		SpringApplication.run(TwsToAwsApplication.class, args);
	}

	/**
	 * <pre>
	 * Endpoing to invoke:
	 * curl localhost:8080/uppercase -d '"Alpha_Beta_Gamma"' -H "Content-Type: application/json"
	 * </pre>
	 * 
	 * @return
	 */
	@Bean
	public Function<String, String> uppercase() {
		return req -> req.toUpperCase();
	}

	/**
	 * <pre>
	 * Endpoing to invoke:
	 * curl localhost:8080/lowercase -d '"Alpha_Beta_Gamma"' -H "Content-Type: application/json"
	 * </pre>
	 * 
	 * @return
	 */
	@Bean
	public Function<String, String> lowercase() {
		return req -> req.toLowerCase();
	}
}
