package bftsmart.runtime.quorum;

import bftsmart.reconfiguration.util.HostsConfig;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class H {

    LinkedHashSet<Integer> hosts = new LinkedHashSet<>();

    public void addHost(Integer h)
    {
        hosts.add(h);
    }

    // returns the first size hosts from existing hosts
    public H pickFirst(int size) throws IllegalArgumentException
    {
        if(size > hosts.size())
            throw new IllegalArgumentException("wrong number of hosts. size cannot be larger than the size of the H");
        H output = new H();
        Iterator<Integer> iterator = hosts.iterator();
        while(size-- > 0)
            output.addHost(iterator.next());
        return output;
    }

    public int[] toIntArray()
    {
        return hosts.stream().mapToInt(Integer::intValue).toArray();
    }

    public static H union(H hosts1, H hosts2)
    {
        H output = new H();
        output.hosts.addAll(hosts1.hosts);
        output.hosts.addAll(hosts2.hosts);
        return output;
    }

    public static H fromIntArray(int[] arr)
    {
        H host = new H();
        for (int i: arr) {
            host.addHost(i);
        }
        return host;
    }

    // checks if this object is subset of the given quorum q.
    public boolean isSubset(Quorum q){
        if(q.nodes.size() > hosts.size())
            return false;
        for (int h : hosts)
        {
            if(!q.nodes.contains(h))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return hosts.toString();
    }
}
