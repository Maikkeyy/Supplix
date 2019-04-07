package supplier;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LaunchSuppliers {

    public static void main(String[] argv) throws Exception {

        for(int i = 0; i < 4; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Supplier supplier = new Supplier(); // launch supplier
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
