package bftsmart.usecase;

public class Config{
    public String name;
    public String className;
    public String cluster;

    public Config(String name, String className, String clusterId) {
        this.name = name;
        this.className = className;
        this.cluster = clusterId;
    }
}