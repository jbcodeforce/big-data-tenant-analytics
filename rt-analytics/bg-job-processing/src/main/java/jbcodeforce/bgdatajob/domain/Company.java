package jbcodeforce.bgdatajob.domain;

public class Company {
    public String company_id;
    public String industry;
    public Integer revenu;
    public Integer employees;
    public Integer job30;
    public Integer job90;
    public Double monthlyFee; 
    public Double totalFee;
    public Boolean riskOfChurn;

    public Company() {
        super();
    }

    public String toCSV() {
        return company_id + "," 
              + industry + ","
              + revenu.toString() + ","
              + employees.toString() + ","
              + job30.toString() + ","
              + job90.toString() + ","
              + monthlyFee.toString() + ","
              + totalFee.toString() + ","
              + riskOfChurn.toString();
    }
}
