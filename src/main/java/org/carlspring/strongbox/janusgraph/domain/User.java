package org.carlspring.strongbox.janusgraph.domain;

import java.util.Date;

/**
 * @author carlspring
 */
public interface User extends DomainObject
{

    String LABEL = "User";

    String getUsername();

    String getPassword();

//    Set<String> getRoles();
//
//    String getSecurityTokenKey();

    boolean isEnabled();

    Date getLastUpdated();

//    String getSourceId();

}
