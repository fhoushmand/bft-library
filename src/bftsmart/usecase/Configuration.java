package bftsmart.usecase;

import bftsmart.runtime.quorum.H;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Configuration{
    private String principalName;
    private String className;
    private H hostSet;

    public Configuration(String name, String className, String hosts) {
        this.principalName = name;
        this.className = className;
        hostSet = new H(name);
        for (String h : hosts.split(","))
            hostSet.addHost(Integer.valueOf(h));
    }

    public Configuration(String name, String className) {
        this.principalName = name;
        this.className = className;
        hostSet = new H(name);
    }

    public void addHostSet(ArrayList<Integer> hosts){
        for(Integer h: hosts){
            hostSet.addHost(h);
        }
        return;
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