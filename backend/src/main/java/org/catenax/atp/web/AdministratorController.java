package org.catenax.atp.web;


import org.catenax.atp.config.security.SecurityConfigProperties;
import org.catenax.atp.endpoints.request.DeleteGroupRequest;
import org.catenax.atp.service.CacheService;
import org.catenax.atp.utils.constant.AppConstants;
import org.catenax.atp.validator.ValueMatcher;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(AppConstants.APP_CONTEXT_PATH + "/admin")
@Validated
public class AdministratorController {



    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private Keycloak keycloakClient;

    @Autowired
    private SecurityConfigProperties configProperties;

    @GetMapping("/endpoints")
    public ResponseEntity<List<String>> getEndpoints(@Valid @ValueMatcher(expectedValue = "password") @RequestHeader(value = "private-key") String privateKey) {
        return new ResponseEntity<>(this.fetchRolesFromAPIs(), HttpStatus.OK);
    }

    /**
     * Add keycloak role and client to application cache
     */
    @PutMapping("/group/cache")
    public void updateRoleToCache(@Valid @ValueMatcher(expectedValue = "password") @RequestHeader(value = "private-key") String privateKey) {
        GroupsResource groups = this.keycloakClient.realm(this.configProperties.realm()).groups();
        for (GroupRepresentation groupRepresentation : groups.groups()) {
            GroupRepresentation gp = groups.group(groupRepresentation.getId()).toRepresentation();

            Map<String, List<String>> clientRole = gp.getClientRoles();
            if(clientRole != null){
                this.cacheService.addRolesToGroup(gp.getName(), clientRole.get(this.configProperties.clientId()),false);
            }
        }
    }

    /**
     * Remove Group info from cache based on groupName
     *
     */
    @DeleteMapping("/group")
    public void deleteGroupFromCache(@Valid @ValueMatcher(expectedValue = "password") @RequestHeader(value = "private-key") String privateKey,
                                     @Valid @RequestBody DeleteGroupRequest deleteGroupRequest){
        this.cacheService.removeGroup(deleteGroupRequest.groupName());
    }

    /**
     * Add all API as role in keycloak client
     *
     */
    @PutMapping("/role/keycloak")
    public void updateRoleToKeycloak(@Valid @ValueMatcher(expectedValue = "password") @RequestHeader(value = "private-key") String privateKey){
        List<String> apiLists=fetchRolesFromAPIs();
        ClientsResource clientResource = this.keycloakClient.realm(this.configProperties.realm()).clients();
        List<ClientRepresentation> clientRepresentations= clientResource.findByClientId(this.configProperties.clientId());
        List<String> existingRoles = new ArrayList<>();
        ClientResource client;
        if(!clientRepresentations.isEmpty()){
            String id = clientRepresentations.get(0).getId();
            client = clientResource.get(id);
            List<RoleRepresentation> roleRepresentations = client.roles().list();
            roleRepresentations.forEach(e -> existingRoles.add(e.getName()));
        } else {
            client = null;
        }
        if(existingRoles.size() > 0){
            apiLists.removeAll(existingRoles);
        }
        if(client != null){
            apiLists.forEach(e -> {
                RoleRepresentation roleRepresentation = new RoleRepresentation();
                roleRepresentation.setName(e);
                roleRepresentation.setDescription("Auto Created from API");
                client.roles().create(roleRepresentation);
            });
        }
    }


    /**
     * Fetch All APIS from RequestMapping Bean
     *
     */
    private List<String> fetchRolesFromAPIs() {
        List<String> ls = new ArrayList<>();
        this.requestMappingHandlerMapping
                .getHandlerMethods()
                .keySet()
                .stream()
                .filter(p -> p.getActivePatternsCondition().toString().contains(AppConstants.APP_CONTEXT_PATH))
                .forEach(e -> ls.add(this.fetchRolesFromApis(e)));
        return ls;
    }

    /**
     * Prepare role from API Info
     */
    private String fetchRolesFromApis(RequestMappingInfo rmInfo) {
        StringBuilder builder = new StringBuilder();
        if (!rmInfo.getMethodsCondition().isEmpty()) {
            Set<RequestMethod> httpMethods = rmInfo.getMethodsCondition().getMethods();
            builder.append(httpMethods.size() == 1 ? httpMethods.iterator().next().toString().toLowerCase() : httpMethods);
        }
        builder.append(rmInfo.getActivePatternsCondition());
        return builder.toString().replace("/", "_").replaceAll("\\[|\\]", "").replaceAll("\\{|\\}", "-");
    }
}
