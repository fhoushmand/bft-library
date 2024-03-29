package bftsmart.runtime.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class IntIntPair implements Externalizable {
    Integer first;
    Integer second;

    public IntIntPair()
    {

    }

    public IntIntPair(int f, int s)
    {
        first = f;
        second = s;
    }

    public Integer getFirst() {
        return first;
    }

    public Integer getSecond() {
        return second;
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
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        first = in.readInt();
        second = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntIntPair)) return false;
        IntIntPair that = (IntIntPair) o;
        return Objects.equals(getFirst(), that.getFirst());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst());
    }

    @Override
    public String toString() {
        return "<" + first + "," + second + ">";
    }
}
