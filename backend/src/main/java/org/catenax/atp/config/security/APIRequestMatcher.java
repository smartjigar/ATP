package org.catenax.atp.config.security;


import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import org.catenax.atp.exception.BadDataException;
import org.catenax.atp.service.CacheService;
import org.catenax.atp.utils.constant.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class APIRequestMatcher implements RequestMatcher {
    @Autowired
    private CacheService cacheService;

    @Lazy
    @Autowired
    private List<String> apiLists;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public boolean matches(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod().toLowerCase();
        path = path.replace("/", "_");
        String roleRequired = method + path;
        String token = request.getHeader(AppConstants.TOKEN_HEADER_NAME);
        String matchedAPi = null;

        for (String api : this.apiLists) {
            if (matches(roleRequired, api)) {
                matchedAPi = api.replaceAll("\\{|\\}", "-");
                break;
            }
        }

        if (matchedAPi != null && !StringUtils.isEmpty(token)) {
            JWTClaimsSet claimsSet = this.fetchClaimSetFromToken(token);
            if (claimsSet == null) {
                throw new BadDataException("Invalid Token","ATP-02-01");
            }
            ArrayList groupsName = (ArrayList) claimsSet.getClaim(AppConstants.CLAIMS_GROUP_NAME);
            if (groupsName != null && groupsName.size() > 0) {
                List redisRolesWithGroup = this.cacheService.getRolesFromGroupName(groupsName);

                if(!redisRolesWithGroup.contains(matchedAPi)){
                    throw new BadDataException("Token Authorization Failed","ATP-02-03");
                }
                return true;
            }else {
                log.info("No Group found in claims");
                throw new BadDataException("Token Authorization Failed","ATP-02-02");
            }
        }
        return false;
    }

    public static boolean matches(String requestUri, String actuatorEndpoint) {
        return PATH_MATCHER.match(actuatorEndpoint, requestUri);
    }

    private JWTClaimsSet fetchClaimSetFromToken(String token) {
        if (token.contains(AppConstants.TOKEN_PREFIX)) {
            token = token.substring(token.indexOf(" "));
        } else {
            return null;
        }
        try {
            JWT jwt = JWTParser.parse(token);
            return jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
