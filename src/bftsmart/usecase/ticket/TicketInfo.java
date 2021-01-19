package bftsmart.usecase.ticket;

import bftsmart.usecase.auction.OfferInfo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class TicketInfo implements Externalizable { ;
    public String scheduleInfo;
    public Integer price;

    public TicketInfo() {
    }

    public TicketInfo(String sched, Integer p)
    {
        scheduleInfo = sched;
        price = p;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(scheduleInfo);
        out.writeInt(price);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        scheduleInfo = (String) in.readObject();
        price = in.readInt();
    }

    @Override
    public String toString() {
        return "[" + scheduleInfo + ":" + price + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketInfo)) return false;
        TicketInfo ticketInfo = (TicketInfo) o;
        return toString().equals(ticketInfo.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
