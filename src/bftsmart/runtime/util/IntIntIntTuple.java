package bftsmart.runtime.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class IntIntIntTuple implements Externalizable {
    Integer first;
    Integer second;
    Integer third;

    public IntIntIntTuple()
    {

    }

    public IntIntIntTuple(int f, int s, int t)
    {
        first = f;
        second = s;
        third = t;
    }

    public Integer getFirst() {
        return first;
    }

    public Integer getSecond() {
        return second;
    }

    public Integer getThird() {
        return third;
    }

    public void setThird(Integer third) {
        this.third = third;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(first);
        out.writeInt(second);
        out.writeInt(third);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        first = in.readInt();
        second = in.readInt();
        third = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntIntIntTuple)) return false;
        IntIntIntTuple that = (IntIntIntTuple) o;
        return Objects.equals(getFirst(), that.getFirst());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst());
    }

    @Override
    public String toString() {
        return "<" + first + "," + second + "," + third + ">";
    }
}
