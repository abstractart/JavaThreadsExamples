import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class ThreadExampleWithPid {

    // Интерфейс для использования системного вызова gettid
    public interface CLibrary extends Library {
        CLibrary INSTANCE = Native.load(Platform.isLinux() ? "c" : "msvcrt", CLibrary.class);

        long syscall(long number, Object... args);
    }

    private static final long SYS_gettid = 186; // Код системного вызова gettid для Linux

    public static void work(int seconds) {
        long pid = getProcessId();
        long nativeTid = getNativeThreadId();
        System.out.println("Child thread pid: " + pid + " | Child thread native id: " + nativeTid);
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + nativeTid);
        }
    }

    public static void runThreads(int seconds, int threadsCount) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadsCount; i++) {
            Thread t = new Thread(() -> work(seconds));
            t.start();
            threads.add(t);
        }

        // Ждем завершения всех потоков
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Thread join interrupted: " + t.getId());
            }
        }
    }

    public static long getProcessId() {
        // Получение PID процесса
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    public static long getNativeThreadId() {
        // Вызов системного вызова gettid через JNA
        return CLibrary.INSTANCE.syscall(SYS_gettid);
    }

    public static void main(String[] args) {
        int threadsCount = 10;
        int seconds = 60;

        long pid = getProcessId();
        long nativeTid = getNativeThreadId();
        System.out.println("Main thread with pid: " + pid + " | Main thread native id: " + nativeTid);
        System.out.println("Press Enter to continue...");
        try {
            System.in.read(); // Ожидаем нажатия Enter
        } catch (Exception e) {
            System.out.println("Error reading input");
        }

        runThreads(seconds, threadsCount);
    }
}
