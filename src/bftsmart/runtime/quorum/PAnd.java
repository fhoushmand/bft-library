package bftsmart.runtime.quorum;

public class PAnd extends Q {

    P p1;
    P p2;

    public PAnd(P p1, P p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean isSubset(Quorum q) {
        return p1.isSubset(q) && p2.isSubset(q);
    }

    @Override
    public String toString() {
        return p1 + "&&" + p2;
    }
}
