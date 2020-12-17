package bftsmart.runtime.quorum;

public class Q {

    H referenceHosts;
    int size;

    public Q()
    {

    }
    public Q(H hosts, int size)
    {
        referenceHosts = hosts;
        this.size = size;
    }

    // checks if this object is subset of the given quorum q.
    public boolean isSubset(Quorum q){
        if(q.nodes.size() < (referenceHosts.hosts.size()-size))
            return false;
        int count = 0;
        for (int n : q.nodes)
        {
            if(referenceHosts.hosts.contains(n))
                count++;
        }
        return count >= size;
    }

}
