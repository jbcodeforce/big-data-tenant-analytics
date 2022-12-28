package jbcodeforce.saas.tm.api.dto;

/**
 * Data Transfer Object for the API
 */
public class TenantDTO {
    public String tenantID;
    public String industry;
    public Integer revenu;
    public Integer employees;

    public TenantDTO() {
        super();
    }

    public TenantDTO(String tenantID, String industry, Integer revenu, Integer employees) {
        this.tenantID = tenantID;
        this.industry = industry;
        this.revenu = revenu;
        this.employees = employees;
    }

    
}
