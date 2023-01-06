package jbcodeforce.bgdatajob;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kinesis.sink.KinesisStreamsSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.kinesis.shaded.com.amazonaws.regions.DefaultAwsRegionProviderChain;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.config.AWSConfigConstants;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;

import jbcodeforce.bgdatajob.domain.Company;
import jbcodeforce.bgdatajob.domain.JobEvent;
import jbcodeforce.bgdatajob.external.ChurnResponse;
import jbcodeforce.bgdatajob.utils.JsonDeserializationSchema;
import jbcodeforce.bgdatajob.utils.JsonSerializationSchema;

/**
 * 
 */
public class JobEventProcessing {
    private static final Logger LOG = LoggerFactory.getLogger(JobEventProcessing.class);
    private static final String DEFAULT_REGION_NAME = new DefaultAwsRegionProviderChain().getRegion();
    private static  ObjectMapper mapper = new ObjectMapper();
    private static SourceFunction<JobEvent> createJobEventDataStream(ParameterTool parameters) throws IOException {
        Properties props = new Properties();
        props.setProperty(ConsumerConfigConstants.AWS_REGION, parameters.get("aws.region", DEFAULT_REGION_NAME));
        props.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION,
                parameters.get("jobs.stream.initial.position", "LATEST"));
        props.setProperty(AWSConfigConstants.AWS_CREDENTIALS_PROVIDER, "AUTO");
        props.setProperty(ConsumerConfigConstants.SHARD_GETRECORDS_INTERVAL_MILLIS, "1000");
        props.setProperty(ConsumerConfigConstants.SHARD_USE_ADAPTIVE_READS, "true");
        return new FlinkKinesisConsumer<>(parameters.getRequired("jobs.stream.name"),
                new JsonDeserializationSchema<>(JobEvent.class),
                props);
    }

    private static StreamingFileSink<String> createS3Sink(ParameterTool parameters) {
        return StreamingFileSink
                .forRowFormat(new Path(parameters.getRequired("S3SinkPath")), new SimpleStringEncoder<String>("UTF-8"))
                .build();
    }

    private static KinesisStreamsSink<Company> createSinkToKinesis(ParameterTool parameters) throws IOException {
        return KinesisStreamsSink.<Company>builder()
                .setKinesisClientProperties(
                        KinesisAnalyticsRuntime.getApplicationProperties().get("ProducerConfigProperties"))
                .setSerializationSchema(new JsonSerializationSchema<Company>())
                .setStreamName(parameters.get("enrichedcompanies.stream.name", "enrichedcompanies"))
                .setPartitionKeyGenerator(element -> String.valueOf(element.hashCode()))
                .build();
    }

    /**
     *
     * @return
     */
    private static Company callRemoteChurnScoring(Company company,ParameterTool parameters) {
        URI predictChurnEndpoint = URI.create(parameters.getRequired("predictChurnApiEndpoint"));
        try {
            HttpRequest request = HttpRequest.newBuilder(predictChurnEndpoint)
            .POST(HttpRequest.BodyPublishers.ofByteArray(mapper.writeValueAsBytes(company)))
            .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
            ChurnResponse churnAssessment = mapper.readValue(response.body(), ChurnResponse.class);
            company.riskOfChurn = Boolean.valueOf(churnAssessment.churn);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return company;
    }

    private static Company callRemoteCompanyManagerService(JobEvent event,ParameterTool parameters) {
        Company c = new Company();
        c.companyID = event.companyID;
        c.employees = 4635;
        c.industry = "retail";
        c.job30 = 10 + event.nbJobs;
        c.job90 = 100 + event.nbJobs;
        c.revenu = 100000;
        c.monthlyFee = 400.00;
        c.totalFee = 1200.00;
        c.riskOfChurn = Boolean.FALSE;
        return c;
    }

    public static void main(String[] args) throws Exception {

        LOG.info(("@@@ Starting Flink DAG creation"));
      

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        ParameterTool parameters = getApplicationParameters();
       
        // Map<String,String> apiKeyHeader = ImmutableMap.of("x-api-key",
        // parameters.getRequired("predictChurnApiKey"));

        DataStream<JobEvent> jobStreams = env.addSource(createJobEventDataStream(parameters));
        // apply here any filtering logic
        // ...
        // Build HTTP request to call Company Enrichment from Tenant Service
        /**
         * DataStream<HttpRequest<Company>> callTenantServiceRequest =
         * jobStreams.map(jobEvent -> {
         * String[] words = jobEvent.split(",");
         * Company companyToEnrich = new Company(words[0]);
         * return new HttpRequest<Company>(companyToEnrich,SdkHttpMethod.GET)
         * .withRawQueryParameter("companyID", companyToEnrich.companyID);
         * });
         * Replaced by mockup call
         */
        DataStream<Company> companyStream = jobStreams
                .filter(jobEvent -> JobEvent.class.isAssignableFrom(jobEvent.getClass()))
                .map(jobEvent -> callRemoteCompanyManagerService(jobEvent,parameters));
       
        DataStream<Company> enrichedCompany = companyStream.map(company -> callRemoteChurnScoring(company,parameters));
        enrichedCompany.map(company -> company.toCSV()).addSink(createS3Sink(parameters));
        enrichedCompany.sinkTo(createSinkToKinesis(parameters));
        env.execute();
    }

    private static ParameterTool getApplicationParameters() throws IOException {

        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();

        Properties specificProperties = applicationProperties.get("ApplicationConfigProperties");
        Properties producerProperties = applicationProperties.get("ProducerConfigProperties");
        Properties consumerProperties = applicationProperties.get("ConsumerConfigProperties");
        Map<String, String> map = new HashMap<>(producerProperties.size() + consumerProperties.size());
        specificProperties.forEach((k, v) -> map.put((String) k, (String) v));
        producerProperties.forEach((k, v) -> map.put((String) k, (String) v));
        consumerProperties.forEach((k, v) -> map.put((String) k, (String) v));
        return ParameterTool.fromMap(map);
    }

    /*
     * DataStream<HttpRequest<Company>> predictChurnRequest = companyStream.map(company -> {
            return new HttpRequest<Company>(company, SdkHttpMethod.POST).withBody(mapper.writeValueAsString(company));
        })
                .returns(new TypeHint<HttpRequest<Company>>() {
                });
        ;

        DataStream<HttpResponse<Company>> predictChurnResponse =
                // Asynchronously call Endpoint
                AsyncDataStream.unorderedWait(
                        predictChurnRequest,
                        new Sig4SignedHttpRequestAsyncFunction<>(predictChurnEndpoint),
                        30, TimeUnit.SECONDS, 20)
                        .returns(new TypeHint<HttpResponse<Company>>() {
                        });

        DataStream<Company> enrichedCompany = predictChurnResponse
                // Only keep successful responses for enrichment, which have a 200 status code
                .filter(response -> response.statusCode == 200)
                // Enrich Company with response from predictChurn
                .map(response -> {
                    boolean expectedChurn = mapper.readValue(response.responseBody, ObjectNode.class).get("churn")
                            .asBoolean();
                    return response.triggeringEvent.withExpectedChurn(expectedChurn);
                });
     */
}
