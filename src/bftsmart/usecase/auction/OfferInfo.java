package bftsmart.usecase.auction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class OfferInfo implements Externalizable {
    public String airlineName;
    public String seatInfo;
    //public Integer offer;

    public OfferInfo() {
    }

    public OfferInfo(String airlineName, String seatInfo) {
        this.airlineName = airlineName;
        this.seatInfo = seatInfo;
        //this.offer = offer;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(airlineName);
        out.writeObject(seatInfo);
        //out.writeInt(offer);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        airlineName = (String) in.readObject();
        seatInfo = (String) in.readObject();
        //offer = in.readInt();
    }

    @Override
    public String toString() {
        return airlineName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OfferInfo)) return false;
        OfferInfo offerInfo = (OfferInfo) o;
        return airlineName.equals(offerInfo.airlineName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airlineName);
    }

    //public String getAsString()
    //{
        //return airlineName + "<" + seatInfo + ":" + offer + ">";
    //}

    public String getAsString()
    {
        return airlineName + "<" + seatInfo + ":>";
    }
}
