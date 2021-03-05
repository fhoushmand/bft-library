package bftsmart.usecase;

public class Configuration{
    private String principalName;
    private String className;
    private int[] hostSet;
    private String cluster;

    public Configuration(String name, String className, String hosts, String clusterId) {
        this.principalName = name;
        this.className = className;
        hostSet = new int[hosts.split(",").length];
        int count = 0;
        for (String h : hosts.split(","))
            hostSet[count++] = Integer.valueOf(h);
        this.cluster = clusterId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getClassName() {
        return className;
    }

    public int[] getHostSet() {
        return hostSet;
    }

    public String getCluster() {
        return cluster;
    }
}