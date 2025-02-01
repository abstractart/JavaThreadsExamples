import java.io.*;
import java.time.Duration;
import java.util.concurrent.*;

public class MultiProcessExample {
    public static void main(String[] args) {
        // Выводим PID родительского процесса
        long parentPid = ProcessHandle.current().pid();
        System.out.println("Родительский процесс PID: " + parentPid);

        int numProcesses = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numProcesses); // Пул потоков

        for (int i = 0; i < numProcesses; i++) {
            executor.submit(() -> {
                try {
                    // Запускаем дочерний процесс, который выполняет другой Java-класс
                    ProcessBuilder processBuilder = new ProcessBuilder(
                            "java", "-cp", System.getProperty("java.class.path"), ChildProcess.class.getName()
                    );
                    Process process = processBuilder.start();

                    // Читаем вывод дочернего процесса (его PID)
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("Дочерний процесс: " + line);
                        }
                    }

                    // Ждем завершения процесса
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // Закрываем пул потоков после завершения задач
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Дочерний класс, который будет запущен в новом процессе
    public static class ChildProcess {
        public static void main(String[] args) {
            System.out.println("PID: " + ProcessHandle.current().pid());
            try {
                Thread.sleep(Duration.ofMinutes(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
