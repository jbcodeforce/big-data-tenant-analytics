package jbcodeforce.saas.tm.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jbcodeforce.saas.tm.api.dto.TenantDTO;

@Entity
public class Tenant extends PanacheEntityBase {

    @Id
    public String tenantID;
    public String industry;
    public Integer revenu;
    public Integer employees;
    public Integer job30;
    public Integer job90;
    public Double monthlyFee; 
    public Double totalFee;
    public Boolean riskOfChurn;

    public Tenant() {
        super();
    }

    public Tenant(String companyID, String industry, Integer revenu, Integer employees, Integer job30, Integer job90,
            Double monthlyFee, Double totalFee, Boolean riskOfChurn) {
        this.tenantID = companyID;
        this.industry = industry;
        this.revenu = revenu;
        this.employees = employees;
        this.job30 = job30;
        this.job90 = job90;
        this.monthlyFee = monthlyFee;
        this.totalFee = totalFee;
        this.riskOfChurn = riskOfChurn;
    }

    public static Tenant copy(Tenant inTenant) {
        Tenant t = new Tenant(inTenant.tenantID,
                    inTenant.industry,
                    inTenant.revenu,
                    inTenant.employees,
                    inTenant.job30,
                    inTenant.job90,
                    inTenant.monthlyFee,
                    inTenant.totalFee,
                    inTenant.riskOfChurn);
        return t;
    }

    public static Tenant fromDTO(TenantDTO aNewTenantDTO) {
        return new Tenant(aNewTenantDTO.tenantID,
                aNewTenantDTO.industry,
                aNewTenantDTO.revenu,
                aNewTenantDTO.employees,0,0,0.0,0.0,false);
    }
    
}
