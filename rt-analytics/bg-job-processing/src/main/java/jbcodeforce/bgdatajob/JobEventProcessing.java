package jbcodeforce.bgdatajob;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.Path;
import org.apache.flink.kinesis.shaded.com.amazonaws.regions.DefaultAwsRegionProviderChain;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.config.AWSConfigConstants;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.ibm.icu.impl.number.DecimalFormatProperties.ParseMode;

import jbcodeforce.bgdatajob.domain.Company;
import jbcodeforce.bgdatajob.operators.Sig4SignedHttpRequestAsyncFunction.HttpRequest;
import software.amazon.awssdk.http.SdkHttpMethod;

/**
 * 
 */
public class JobEventProcessing {

    private static final String DEFAULT_REGION_NAME = new DefaultAwsRegionProviderChain().getRegion();


    private static SourceFunction<String> createJobEventDataStream( ParameterTool parameters) throws IOException {
        Properties props = new Properties();
        props.setProperty(ConsumerConfigConstants.AWS_REGION, parameters.get("Region", DEFAULT_REGION_NAME));
        props.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
        props.setProperty(AWSConfigConstants.AWS_CREDENTIALS_PROVIDER, "AUTO");
        props.setProperty(ConsumerConfigConstants.SHARD_GETRECORDS_INTERVAL_MILLIS, "1000");
        props.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
        props.setProperty(ConsumerConfigConstants.SHARD_USE_ADAPTIVE_READS, "true");


        return new FlinkKinesisConsumer<>(parameters.getRequired("JobEventStreamName"), new SimpleStringSchema(), props);
    }

    private static StreamingFileSink<String> createS3Sink(ParameterTool parameters) {
        return StreamingFileSink
                .forRowFormat(new Path(parameters.getRequired("S3SinkPath")), new SimpleStringEncoder<String>("UTF-8"))
                .build();
    }
    
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        ParameterTool parameters = getApplicationParameters();
        
        DataStream<String> jobStreams = env.addSource(createJobEventDataStream(parameters));
        // apply here any filtering logic
        // ...
        // Build HTTP request to call Inference Scoring Service
        DataStream<HttpRequest<String>> predictChurnRequest = jobStreams.map(jobEvent ->  {String[] words = jobEvent.split(",");
                return new HttpRequest<String>(jobEvent,SdkHttpMethod.GET);
                });
        
        DataStream<Company> enrichedStream = jobStreams.map(jobEvent ->  {String[] words = jobEvent.split(",");
                Company c = getCompanyRecord(words[0]);
                return c;
            });
        enrichedStream.map(company -> company.toCSV()).addSink(createS3Sink(parameters));
        env.execute();
    }


    public static ParameterTool getApplicationParameters() throws IOException {
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        Properties flinkProperties = applicationProperties.get("FlinkApplicationProperties");
        Map<String, String> map = new HashMap<>(flinkProperties.size());
        flinkProperties.forEach((k, v) -> map.put((String) k, (String) v));
        return ParameterTool.fromMap(map);
    }

    public static Company getCompanyRecord(String cid) {
        Company c = new Company();
        c.company_id = cid;
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
