package bftsmart.usecase;

import bftsmart.runtime.quorum.H;

public class Configuration{
    private String principalName;
    private String className;
    private H hostSet;

    public Configuration(String name, String className, String hosts, String clusterId) {
        this.principalName = name;
        this.className = className;
        hostSet = new H(name);
        for (String h : hosts.split(","))
            hostSet.addHost(Integer.valueOf(h));
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getClassName() {
        return className;
    }

    public H getHostSet() {
        return hostSet;
    }
}