package jbcodeforce.bgdatajob;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.Path;
import org.apache.flink.kinesis.shaded.com.amazonaws.regions.DefaultAwsRegionProviderChain;
import org.apache.flink.kinesis.shaded.software.amazon.awssdk.utils.ImmutableMap;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
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
import jbcodeforce.bgdatajob.operators.Sig4SignedHttpRequestAsyncFunction;
import jbcodeforce.bgdatajob.operators.Sig4SignedHttpRequestAsyncFunction.HttpRequest;
import jbcodeforce.bgdatajob.operators.Sig4SignedHttpRequestAsyncFunction.HttpResponse;
import software.amazon.awssdk.http.SdkHttpMethod;
/**
 * 
 */
public class JobEventProcessing {
    private static final Logger LOG = LoggerFactory.getLogger(JobEventProcessing.class);
    private static final String DEFAULT_REGION_NAME = new DefaultAwsRegionProviderChain().getRegion();


    private static SourceFunction<String> createJobEventDataStream( ParameterTool parameters) throws IOException {
        Properties props = new Properties();
        props.setProperty(ConsumerConfigConstants.AWS_REGION, parameters.get("aws.region", DEFAULT_REGION_NAME));
        props.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, parameters.get("jobs.stream.initial.position","LATEST"));
        props.setProperty(AWSConfigConstants.AWS_CREDENTIALS_PROVIDER, "AUTO");
        props.setProperty(ConsumerConfigConstants.SHARD_GETRECORDS_INTERVAL_MILLIS, "1000");
        props.setProperty(ConsumerConfigConstants.SHARD_USE_ADAPTIVE_READS, "true");


        return new FlinkKinesisConsumer<>(parameters.getRequired("jobs.stream.name"), new SimpleStringSchema(), props);
    }

    private static StreamingFileSink<String> createS3Sink(ParameterTool parameters) {
        return StreamingFileSink
                .forRowFormat(new Path(parameters.getRequired("S3SinkPath")), new SimpleStringEncoder<String>("UTF-8"))
                .build();
    }
    
    public static void main(String[] args) throws Exception {
        
        LOG.info(("@@@ Starting Flink DAG creation"));
        ObjectMapper mapper = new ObjectMapper();
      

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
      
        ParameterTool parameters = getApplicationParameters();
        URI predictChurnEndpoint = URI.create(parameters.getRequired("predictChurnApiEndpoint") + "assessChurn");
        Map<String,String> apiKeyHeader = ImmutableMap.of("x-api-key", parameters.getRequired("ApiKey"));
  
        DataStream<String> jobStreams = env.addSource(createJobEventDataStream(parameters));
        // apply here any filtering logic
        // ...
        // Build HTTP request to call Company Enrichment from Tenant Service
        /* 
        DataStream<HttpRequest<Company>> callTenantServiceRequest = jobStreams.map(jobEvent ->  {
                String[] words = jobEvent.split(",");
                Company companyToEnrich = new Company(words[0]);
                return new HttpRequest<Company>(companyToEnrich,SdkHttpMethod.GET)
                        .withRawQueryParameter("companyID", companyToEnrich.companyID);
                });
        Replaced by mockup call */
        DataStream<Company> enrichedStream = jobStreams.map(jobEvent ->  {String[] words = jobEvent.split(",");
                Company c = getCompanyRecord(words[0]);
                return c;
        });
        // Build HTTP request to call Inference Scoring Service
        DataStream<HttpRequest<Company>> predictChurnRequest = enrichedStream.map(company ->  {
              
                return new HttpRequest<Company>(company,SdkHttpMethod.POST).withBody(company.toCSV());
            });
            DataStream<HttpResponse<Company>> predictChurnResponse =
            // Asynchronously call predictFare Endpoint
            AsyncDataStream.unorderedWait(
                predictChurnRequest,
                new Sig4SignedHttpRequestAsyncFunction<>(predictChurnEndpoint, apiKeyHeader),
                30, TimeUnit.SECONDS, 20
            )
            .returns(new TypeHint<HttpResponse<Company>>() {});

        DataStream<Company> enrichedCompany = predictChurnResponse
            // Only keep successful responses for enrichment, which have a 200 status code
            .filter(response -> response.statusCode == 200)
            // Enrich RideRequest with response from predictFareEndpoint
            .map(response -> {
                boolean expectedChurn = mapper.readValue(response.responseBody, ObjectNode.class).get("churn").asBoolean(false);
                return response.triggeringEvent.withExpectedChurn(expectedChurn);
            });
            enrichedCompany.map(company -> company.toCSV()).addSink(createS3Sink(parameters));
        env.execute();
    }


    public static ParameterTool getApplicationParameters() throws IOException {
          
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();

        Properties producerProperties = applicationProperties.get("ProducerConfigProperties");
        Properties consumerProperties = applicationProperties.get("ConsumerConfigProperties");
        Map<String, String> map = new HashMap<>(producerProperties.size() + consumerProperties.size());
        producerProperties.forEach((k, v) -> map.put((String) k, (String) v));
        consumerProperties.forEach((k,v) -> map.put((String) k, (String)v));
        return ParameterTool.fromMap(map);
    }

    public static Company getCompanyRecord(String cid) {
        Company c = new Company();
        c.companyID = cid;
        c.employees = 1000;
        c.industry = "Retail";
        c.job30 = 10;
        c.job90 = 100;
        c.revenu = 2000000;
        c.monthlyFee=550.00;
        c.totalFee=10.00;
        c.riskOfChurn= Boolean.FALSE;
        return c;
    }
}
