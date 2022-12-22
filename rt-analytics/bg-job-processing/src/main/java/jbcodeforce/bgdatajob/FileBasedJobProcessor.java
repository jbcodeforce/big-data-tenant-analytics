package jbcodeforce.bgdatajob;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.api.java.utils.ParameterTool;

/**
 * Running locally use:
 * 
 * flink run -d -c jbcodeforce.bgdatajob.FileBasedJobProcessor
 * /home/bg-job-processing/target/bg-job-processing-1.0.0.jar --companies
 * file:///home/bg-job-processing/data/companies.csv --locations
 * file:///home/bg-job-processing/data/job.csv --output
 * file:///home/bg-job-processing/data/joinout.csv
 */
public class FileBasedJobProcessor {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        ParameterTool params = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(params);

        DataSet<String> companyStream = env.readTextFile(params.get("companies"));
        DataSet<Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer>> companies = companyStream
        
            .map(new MapFunction<String,Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer>>() {
            
                @Override
                public Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer> map(String value) {
                    String[] words = value.split(",");
                    // company_id, industry, revenu, employees, job30, job90, monthly, totalFee
                    return new Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer>(
                        words[0], 
                        words[1],
                        Integer.valueOf(words[2]),
                        Integer.valueOf(words[3]),
                        Integer.valueOf(words[4]),
                        Integer.valueOf(words[5]),
                        Integer.valueOf(words[6]),
                        Integer.valueOf(words[7])
                        );
                }
        });

        DataSet<String> jobStream = env.readTextFile(params.get("jobs"));
        DataSet<Tuple2<String,Integer>> jobs = jobStream.map(new MapFunction<String,Tuple2<String,Integer>>() {
            @Override
            public Tuple2<String,Integer> map(String value) {
                String[] words = value.split(",");
                 // company_id, userid , #job_submitted
                return new Tuple2<String,Integer>(words[0],Integer.valueOf(words[2]));
            }
        });

        DataSet<Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer>> joinedSet = 
            companies.join(jobs)
            .where(0) // indice of the field to be used to do join from first tuple
            .equalTo(0)  // to match the field in idx 0 of the second tuple
            .with( new JoinFunction<Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer>, 
                                    Tuple2<String, Integer>, 
                                    Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer>>() {
                
                public Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer> 
                    join(Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer> company,  
                         Tuple2<String, Integer> job)  {
                     // company_id, industry, revenu, employees, job30 + n, job90 + n, monthly, totalFee
                    return new Tuple8<String,String,Integer,Integer,Integer,Integer,Integer,Integer>(
                        company.f0,   company.f1,  company.f2, company.f3, company.f4 + job.f1, company.f5 + job.f1, company.f6, company.f7);
                    }
            });  

            joinedSet.writeAsCsv(params.get("output"), "\n", " ");
        
        env.execute("JobProcessor Example");

    }
}
