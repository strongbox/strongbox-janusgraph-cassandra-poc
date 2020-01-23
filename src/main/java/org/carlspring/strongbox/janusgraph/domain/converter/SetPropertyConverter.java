package org.carlspring.strongbox.janusgraph.domain.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.typeconversion.AttributeConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author ankit.tomar
 */
public class SetPropertyConverter implements AttributeConverter<Set<String>, String>
{

    private static final String EMPTY_STRING = "";

    private static final String COMMA = ",";

    @Override
    public String toGraphProperty(Set<String> value)
    {
        if (!CollectionUtils.isEmpty(value))
        {
            StringBuilder builder = new StringBuilder();
            for (String element : value)
            {
                builder.append(element).append(COMMA);
            }
            return builder.toString().substring(0, builder.toString().lastIndexOf(COMMA));
        }
        return EMPTY_STRING;
    }

    @Override
    public Set<String> toEntityAttribute(String value)
    {
        if (!StringUtils.isEmpty(value))
        {
            return new HashSet<>(Arrays.asList(value.split(COMMA)));
        }
        return new HashSet<>();
    }

}
