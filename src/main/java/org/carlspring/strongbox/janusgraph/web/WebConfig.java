package org.carlspring.strongbox.janusgraph.web;

import org.carlspring.strongbox.janusgraph.rest.controllers.ControllersConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ControllersConfig.class})
public class WebConfig
{

}
