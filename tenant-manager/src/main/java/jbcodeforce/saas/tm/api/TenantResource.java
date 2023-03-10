package jbcodeforce.saas.tm.api;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jbcodeforce.saas.tm.api.dto.TenantDTO;
import jbcodeforce.saas.tm.domain.Tenant;
import jbcodeforce.saas.tm.infra.TenantRepository;

@ApplicationScoped
@Path("/api/v1/tenants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantResource {
    
    @Inject
    TenantRepository repository;

    @GET
    @Path("/{tenantID}")
    public Tenant get(String tenantID) {
        return repository.findByTenantID(tenantID);
    }

    @GET
    public List<Tenant> getAllTenants() {
        return repository.getAll();
    }

    @POST
    public Response createNewTenant(TenantDTO aNewTenantDTO) {
        Tenant aNewTenant = Tenant.fromDTO(aNewTenantDTO);
        aNewTenant = repository.createNewTenant(aNewTenant);
        return Response.ok(aNewTenant).build();
    }

    @PUT
    @Path("/{tenantID}")
    @Transactional
    public Tenant updateExistingTenant(String tenantID, Tenant aTenant) {
        Tenant entity = repository.findByTenantID(tenantID);
        if(entity == null) {
            throw new NotFoundException();
        }
        entity = Tenant.copy(aTenant);
        return entity;
    }

    @DELETE
    @Path("/{tenantID}")
    @Transactional
    public void delete(String tenantID) {
        Tenant entity = repository.findByTenantID(tenantID);
        if(entity == null) {
            throw new NotFoundException();
        }
        repository.delete(entity);
    }

    @GET
    @Path("/count")
    public Long count() {
        return repository.count();
    }
}
