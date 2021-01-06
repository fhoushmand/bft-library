package bftsmart.runtime.quorum;

import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class Quorum{

    // to mark a quorum as bot and later don't reexecute the method again
    private boolean isBot = false;

    ConcurrentSkipListSet<Integer> nodes;

    public Quorum()
    {
        nodes = new ConcurrentSkipListSet<>();
    }


    public void addNode(int n)
    {
        nodes.add(n);
    }


    public boolean isSuperSetEqual(Q q)
    {
        return q.isSubset(this);
    }

    public boolean isSuperSetEqual(int[] hosts)
    {
        return H.fromIntArray(hosts).isSubset(this);
    }

    public void setBot()
    {
        isBot = true;
        nodes.clear();
    }

    public boolean isBot() {
        return isBot;
    }

    @Override
    public boolean equals(Object obj) {
        Quorum other = (Quorum)obj;
        if(other.nodes.size() != nodes.size())
            return false;

        while (!other.nodes.iterator().hasNext() && !nodes.iterator().hasNext())
        {
            if(other.nodes.iterator().next() != nodes.iterator().next())
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}
