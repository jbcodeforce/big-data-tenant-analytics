package ut;

import jbcodeforce.bgdatajob.JobEventProcessing;
import jbcodeforce.bgdatajob.domain.Company;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class TestCompany {
    public static void main(String[] args) throws JsonProcessingException {
       Company c = JobEventProcessing.getCompanyRecord("comp_4","1");
       ObjectMapper mapper = new ObjectMapper();
       String cAsStr = mapper.writeValueAsString(c);
       System.out.println(cAsStr);
    }
}
