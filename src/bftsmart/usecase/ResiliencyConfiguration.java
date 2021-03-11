package bftsmart.usecase;

import java.util.HashMap;

public class ResiliencyConfiguration {
    private HashMap<String,Integer> resiliencyMap = new HashMap<>();
    private String name;
    public ResiliencyConfiguration(String config)
    {
        this.name = config;
        resiliencyMap.put("Client", 0);
        for(String conf : config.split("-"))
            resiliencyMap.put(conf.substring(0,1), Integer.valueOf(conf.substring(1)));
    }

    public Integer getPrincipalResiliency(String name)
    {
        return resiliencyMap.get(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
