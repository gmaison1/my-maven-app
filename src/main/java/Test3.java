public class Test3 {
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    
    public void method1() {
        synchronized(lock1) {
            int x = 5;
            synchronized(lock2) {
                int y = 10;
            }
            int z = 15;
        }
    }
}
