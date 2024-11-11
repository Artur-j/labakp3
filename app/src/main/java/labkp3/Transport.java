package labkp3;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Transport implements Serializable {
    @Expose
    private String licensePlate;
    @Expose
    private String brand;
    private int year; // transient для уникнення серіалізації під час нативної серіалізації

    public Transport() {
        licensePlate = "";
        brand = "";
        year = 0;
    }

    public Transport(String licensePlate, String brand, int year) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.year = year;
    }

    // Запис інформації у файл з різними потоками
    public void writeToFile(String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("License Plate: " + licensePlate + "\n");
            bw.write("Brand: " + brand + "\n");
        }
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write("Year: " + year + "\n");
        }
    }

    // Читання інформації з файлу
    public static Transport readFromFile(String filePath) throws IOException {
        String licensePlate = null;
        String brand = null;
        int year = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            licensePlate = br.readLine().split(": ")[1];
            brand = br.readLine().split(": ")[1];
            year = Integer.parseInt(br.readLine().split(": ")[1]);
        }

        return new Transport(licensePlate, brand, year);
    }

    // Нативна серіалізація Java
    public void serializeToFile(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        }
    }

    public static Transport deserializeFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Transport) ois.readObject();
        }
    }

    // Запис JSON у файл
    public void writeToJsonFile(String jsonFilePath) throws IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            gson.toJson(this, writer);
        }
    }

    // Запис YAML у файл
    public void writeToYamlFile(String yamlFilePath) throws IOException {
        // Якщо рік випуску до 2010, `brand` буде пропущено
        String brandToSerialize = (year < 2010) ? null : brand;

        // Створення тимчасового об'єкта, в якому пропускається `brand` для старих
        // автомобілів
        Transport tempTransport = new Transport(licensePlate, brandToSerialize, year);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(yamlFilePath)) {
            yaml.dump(tempTransport, writer);
        }
    }

    // Геттери для доступу до полів
    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public int getYear() {
        return year;
    }

    public static void main(String[] args) {
        // Шлях до файлів для тестування
        String textFilePath = "transport.txt";
        String serializedFilePath = "transport.ser";
        String jsonFilePath = "transport.json";
        String yamlFilePath = "transport.yaml";

        // Створення екземпляра класу Transport
        Transport transport = new Transport("ABC123", "Toyota", 2015);

        // Тестування запису в текстовий файл
        try {
            transport.writeToFile(textFilePath);
            System.out.println("Дані успішно записані у файл: " + textFilePath);
        } catch (IOException e) {
            System.err.println("Помилка при запису у файл: " + e.getMessage());
        }

        // Тестування запису в JSON файл
        try {
            transport.writeToJsonFile(jsonFilePath);
            System.out.println("Дані успішно записані у JSON файл: " + jsonFilePath);
        } catch (IOException e) {
            System.err.println("Помилка при запису у JSON файл: " + e.getMessage());
        }

        // Тестування запису в YAML файл
        try {
            transport.writeToYamlFile(yamlFilePath);
            System.out.println("Дані успішно записані у YAML файл: " + yamlFilePath);
        } catch (IOException e) {
            System.err.println("Помилка при запису у YAML файл: " + e.getMessage());
        }

        // Тестування нативної серіалізації
        try {
            transport.serializeToFile(serializedFilePath);
            System.out.println("Дані успішно серіалізовані у файл: " + serializedFilePath);
        } catch (IOException e) {
            System.err.println("Помилка при серіалізації: " + e.getMessage());
        }
    }
}
