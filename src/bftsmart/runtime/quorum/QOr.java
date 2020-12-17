package bftsmart.runtime.quorum;

public class QOr extends Q{

    Q q1;
    Q q2;

    public QOr(Q q1, Q q2)
    {
        this.q1 = q1;
        this.q2 = q2;
    }

    @Override
    public boolean isSubset(Quorum q) {
        return q1.isSubset(q) || q2.isSubset(q);
    }

}
