package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*
        Создаем массив строчек columnMapping, содержащий информацию о предназначении колонок в CVS файле:
        Определяем имя для считываемого CSV файла:
        Получаем список сотрудников, вызвав метод parseCSV(), где в качестве аргументов передаем массив
        строк с информацией о предназначении колонок и названием файла из которого считываем
        Полученный список преобразовываем в строчку в формате JSON и сохраняем в файл
         */
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
    }


    /*
    Метод возвращающий список сотрудников из CSV файла
     */
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        /*
        1. открываем поток считывающий данные из файла
        2. для чтения строк файла CSV передаем поток CSVReader
        пункт 1 и 2 выполняем в блоке try для закрытия потока, после выполнения операций
        3.  Определяем стратегию как нам прочитать наш CSV файл помощью ColumnPositionMappingStrategy
            - определяем класс к которому привязываем данные с CSV документа
            - определяем порядок расположения полей в этом документе
         */
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            /*
            4. Для преобразования данные CSV в объекты java используем CsvToBean(он создает инструмент для взаимодействия CSV документом
            и выбранной ранее стратегии).
                - указываем заданную стратегию для работы со считанными данными
                - создаем с помощью билдера объекты.
             */
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            /*
            5. с помощью парсера "вычитываем объекты" и передаем их в коллекцию и возвращаем их из метода
             */
            List<Employee> staff = csv.parse();
            return staff;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    Метод записывающий список сотрудников Json файла
     */
    private static String listToJson(List<Employee> list) {

        /*
        1. Создаем билдер
        2. С помощью бидлера создаем объект типа
        3. Для преобразования списка объектов Json определяем тип списка (получаем название полей для сопоставления).
        4. Создаем строку в которую сериализуем объект gson с помощью метода toJson()
        5. Создаем файл Json, в которую записываем строку
         */
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        try (FileWriter file = new FileWriter("new_data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}