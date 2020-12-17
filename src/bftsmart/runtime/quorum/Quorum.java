package bftsmart.runtime.quorum;

import java.util.HashSet;

public class Quorum{

    HashSet<Integer> nodes;

    public Quorum()
    {
        nodes = new HashSet<>();
    }


    public void addNode(int n)
    {
        nodes.add(n);
    }


    public boolean isSubsetEqual(Q q)
    {
        return q.isSubset(this);
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
