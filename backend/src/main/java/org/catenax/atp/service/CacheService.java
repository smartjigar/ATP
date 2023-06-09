package org.catenax.atp.service;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheService {

    private final Map<String, List<String>> groupRoleMap = new ConcurrentHashMap<>();

    private void addNewGroup(String groupName, List<String> roles) {
        if(!CollectionUtils.isEmpty(roles)){
            this.groupRoleMap.put(groupName, roles);
        }
    }

    public void removeGroup(String groupName){
        this.groupRoleMap.remove(groupName);
    }

    public List<String> getRolesFromGroupName(String groupName) {
        return this.groupRoleMap.get(groupName);
    }

    public List<String> getRolesFromGroupName(List<String> groupNames) {
        List<String> allRoles = new ArrayList<>();
        groupNames.forEach(e -> {
            List<String> roles = this.getRolesFromGroupName(e);
            if (roles != null && !roles.isEmpty()) {
                allRoles.addAll(roles);
            }
        });
        return allRoles;
    }

    public void addRolesToGroup(String groupName, List<String> newRoles,boolean updateExisting) {
        List<String> roles = this.groupRoleMap.get(groupName);
        if (updateExisting && roles != null && !roles.isEmpty()) {
            roles.addAll(newRoles);
        }else {
            roles= newRoles;
        }
        this.addNewGroup(groupName, roles);
    }
}
