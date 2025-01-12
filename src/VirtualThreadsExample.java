public class VirtualThreadsExample {
    public static void main(String[] args) {
        // Вывод значений установленных свойств
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        System.setProperty("jdk.virtualThreadScheduler.minRunnable", "1");
        System.out.println("Параметры виртуальных потоков:");
        System.out.println("jdk.virtualThreadScheduler.parallelism: " + System.getProperty("jdk.virtualThreadScheduler.parallelism"));
        System.out.println("jdk.virtualThreadScheduler.maxPoolSize: " + System.getProperty("jdk.virtualThreadScheduler.maxPoolSize"));
        System.out.println("jdk.virtualThreadScheduler.minRunnable: " + System.getProperty("jdk.virtualThreadScheduler.minRunnable"));

        // Задача 1: Выполнение с паузой
        Thread task1 = Thread.ofVirtual().start(() -> {
            try {
                System.out.println("Задача 1: Ожидание 3 секунды...");
                Thread.sleep(3000);
                System.out.println("Задача 1: Прошла пауза, выводим текст в консоль.");
            } catch (InterruptedException e) {
                System.err.println("Задача 1 была прервана.");
            }
        });

        // Задача 2: Бесконечный цикл
        Thread task2 = Thread.ofVirtual().start(() -> {
            System.out.println("Задача 2: Начало бесконечного цикла.");
            while (true) {
                // System.out.println("Задача 2: Работаем...");
                // Thread.sleep(1000);
            }
        });

        // Ожидание завершения задачи 1
        try {
            task1.join();
        } catch (InterruptedException e) {
            System.err.println("Ошибка ожидания завершения Задачи 1.");
        }

        // Прерывание задачи 2
        task2.interrupt();
        try {
            task2.join();
        } catch (InterruptedException e) {
            System.err.println("Ошибка ожидания завершения Задачи 2.");
        }

        System.out.println("Главный поток завершён.");
    }
}
