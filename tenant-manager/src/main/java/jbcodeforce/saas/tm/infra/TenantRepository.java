package jbcodeforce.saas.tm.infra;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jbcodeforce.saas.tm.domain.Tenant;

@ApplicationScoped
public class TenantRepository implements PanacheRepository<Tenant>{
    
    public List<Tenant> getAll(){
        return listAll();
    }

    @Transactional
    public Tenant createNewTenant(Tenant aTenant) {
        persist(aTenant);
        return aTenant;
    }

    public Tenant findByTenantID(String tenantID) {
        return find("tenantID",tenantID).firstResult();
    }

    
}
