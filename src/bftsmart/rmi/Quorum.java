package bftsmart.rmi;

import java.util.TreeSet;

public class Quorum {
    TreeSet<Integer> nodes;

    public Quorum()
    {
        nodes = new TreeSet<>();
    }
    public Quorum(int node)
    {
        nodes = new TreeSet<>();
        nodes.add(node);
    }

    public void addNode(int n)
    {
        nodes.add(n);
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
}
