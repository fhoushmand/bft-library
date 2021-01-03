package bftsmart.runtime.quorum;

import bftsmart.usecase.PartitionedObject;


public class QuorumSubsetTest {
    public static void main(String[] args)
    {
        PartitionedObject object = new PartitionedObject();

        for(int i : object.getMethodsH().get("m4"))
            System.out.print(i + " ");
        System.out.println();

        for(int i : object.getMethodsH().get("m2"))
            System.out.print(i + " ");
        System.out.println();

        Quorum rec = new Quorum();
        //from A host
        rec.addNode(0);
        rec.addNode(3);
        rec.addNode(6);
        //from B host
        rec.addNode(7);
        rec.addNode(8);
        rec.addNode(10);

        //must return true
        System.out.println(rec.isSuperSetEqual(object.getMethodsQ().get("ret")));


        Quorum rec2 = new Quorum();
        //from A host
        rec2.addNode(0);
        rec2.addNode(3);
        rec2.addNode(6);
        //from B host
        rec2.addNode(7);


        //must return false
        System.out.println(rec2.isSuperSetEqual(object.getMethodsQ().get("m3")));

    }
}
