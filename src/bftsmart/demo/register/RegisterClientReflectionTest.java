package bftsmart.demo.register;

import java.lang.reflect.Method;

public class RegisterClientReflectionTest {
    public static void main(String[] args) throws Exception {
        BooleanRegisterClient client = new BooleanRegisterClient(0,0);
        Method m = client.getClass().getMethod("write", Boolean.class);
        m.invoke(client, true);
    }
}
