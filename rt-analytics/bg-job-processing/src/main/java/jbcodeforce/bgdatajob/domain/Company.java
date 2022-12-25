package jbcodeforce.bgdatajob.domain;

public class Company {
    public String companyID;
    public String industry;
    public Integer revenu;
    public Integer employees;
    public Integer job30;
    public Integer job90;
    public Double monthlyFee; 
    public Double totalFee;
    public Boolean riskOfChurn;

    public Company(String companyID, String industry, Integer revenu, Integer employees, Integer job30, Integer job90,
            Double monthlyFee, Double totalFee, Boolean riskOfChurn) {
        this.companyID = companyID;
        this.industry = industry;
        this.revenu = revenu;
        this.employees = employees;
        this.job30 = job30;
        this.job90 = job90;
        this.monthlyFee = monthlyFee;
        this.totalFee = totalFee;
        this.riskOfChurn = riskOfChurn;
    }

    public Company() {
        super();
    }

    public Company(String companyID) {
        this.companyID = companyID;
    }

    public String toCSV() {
        return companyID + "," 
              + industry + ","
              + revenu.toString() + ","
              + employees.toString() + ","
              + job30.toString() + ","
              + job90.toString() + ","
              + monthlyFee.toString() + ","
              + totalFee.toString() + ","
              + riskOfChurn.toString();
    }

    public Company withExpectedChurn(boolean expectedChurn) {
        return new Company(this.companyID,
                        this.industry,
                        this.revenu,
                        this.employees,
                        this.job30,
                        this.job90,
                        this.monthlyFee,
                        this.totalFee,
                        expectedChurn);
    }
}
