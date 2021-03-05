package bftsmart.runtime;

import bftsmart.runtime.quorum.Q;

import java.lang.reflect.Method;

public class SplitMethod {
    private String name;
    private int[] hosts;
    private Q communicationQuorum;

    // maybe remove later
    private Method javaMethod;
}
