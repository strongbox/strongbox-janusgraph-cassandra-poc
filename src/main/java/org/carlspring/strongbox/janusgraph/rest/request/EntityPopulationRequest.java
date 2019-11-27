package org.carlspring.strongbox.janusgraph.rest.request;

public class EntityPopulationRequest
{
    private int artifacts;
    private int dependencies;

    public int getArtifacts()
    {
        return artifacts;
    }

    public void setArtifacts(int artifacts)
    {
        this.artifacts = artifacts;
    }

    public int getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(int dependencies)
    {
        this.dependencies = dependencies;
    }
}
