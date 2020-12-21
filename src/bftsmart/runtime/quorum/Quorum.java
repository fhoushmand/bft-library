package bftsmart.runtime.quorum;

import java.util.HashSet;

public class Quorum{

    // to mark a quorum as bot and later don't reexecute the method again
    private boolean isBot = false;

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
