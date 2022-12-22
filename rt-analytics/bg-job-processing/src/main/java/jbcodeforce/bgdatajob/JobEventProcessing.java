package jbcodeforce.bgdatajob;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;


public class JobEventProcessing {

    private static String region = "us-west-2";
    private static String companyStreamName = "companies";
    private static String jobsStreamName = "bigdatajobs";

    public static DataStream<String> createCompanyDataStream(StreamExecutionEnvironment env) throws IOException {
        Properties props = new Properties();
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        props.setProperty(ConsumerConfigConstants.AWS_REGION, region);
        props.setProperty(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");
        return env.addSource(new FlinkKinesisConsumer<>(companyStreamName,new SimpleStringSchema(), applicationProperties.get("ConsumerConfigProperties")));
    }
    
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> companyStreams = createCompanyDataStream(env);
        companyStreams.print();

        env.execute();
    }
}
