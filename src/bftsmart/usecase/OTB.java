package bftsmart.usecase;

// 0,1,2,3,4,5,6 -> A servers??
// 7,8,9,10 -> B servers??
public class OTB extends PartitionedObject{
    public Integer i2 = 5;
    public Boolean a = false;

    public void m2()
    {
        System.out.println("calling ret locally");
        System.out.println("isAccessed value: " + (Boolean)runtime.invokeObj("a", "read", null));
        runtime.invoke("ret", (Integer) runtime.invokeObj("i2", "read", null)); //send ret(i2.read()) message to client;
    }

}
