public class Test2 {
    private Object lock = new Object();
    
    public void method1() {
        int x = 5;
        synchronized(lock) {
            int y = 10;
            int z = x + y;
        }
        int w = 20;
    }
}
