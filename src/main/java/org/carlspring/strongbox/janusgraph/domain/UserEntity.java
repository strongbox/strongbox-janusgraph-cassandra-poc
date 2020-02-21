package org.carlspring.strongbox.janusgraph.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateString;
import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.DATE_FORMAT;

/**
 * @author carlspring
 */
@NodeEntity(User.LABEL)
public class UserEntity extends DomainEntity implements User
{

    private String username;

    private String password;

    private boolean enabled = true;

//    private Set<String> roles = new HashSet<>();
//
//    private String securityTokenKey;

    @DateString(DATE_FORMAT)
    private Date lastUpdated;

//    private String sourceId;

    @Override
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

//    @Override
//    public Set<String> getRoles()
//    {
//        return roles;
//    }
//
//    public void setRoles(Set<String> roles)
//    {
//        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
//    }
//
//    public void addRole(String role)
//    {
//        roles.add(role);
//    }
//
//    public void removeRole(String role)
//    {
//        roles.remove(role);
//    }
//
//    public boolean hasRole(String role)
//    {
//        return roles.contains(role);
//    }
//
//    @Override
//    public String getSecurityTokenKey()
//    {
//        return securityTokenKey;
//    }
//
//    public void setSecurityTokenKey(String securityTokenKey)
//    {
//        this.securityTokenKey = securityTokenKey;
//    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

//    public String getSourceId()
//    {
//        return sourceId;
//    }
//
//    public void setSourceId(String source)
//    {
//        this.sourceId = source;
//    }

}
