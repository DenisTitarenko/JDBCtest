package com.jdbc.tutor;

import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HomeWorkClass {

    private static final String URL = "jdbc:postgresql://localhost:5432/database1";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private static Connection connection;

    public static void main(String[] args) {
        setConnectionAndCreateTable(URL, USER, PASSWORD);
//        Statement statement = connection.createStatement();
//        statement.execute("drop table employees");

        createTable("src/main/resources/create_table.txt");
        try {
            chooseMethod();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void chooseMethod() throws IOException {
        System.out.println("\nChoose what do you want: ");
        System.out.println("1. Add employee to DB;");
        System.out.println("2. Show all employees group by department & date of start work;");
        System.out.println("3. Find employee by name;");
        System.out.println("4. Delete employee by name;");
        System.out.println("5. Increase someone's salary;");
        System.out.println("6. Show names of employees with same salary;");
        System.out.println("0. Exit.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nYour choice: ");
        String input;

        while (true) {
            input = reader.readLine();
            char c = input.charAt(0);

            if (Character.getNumericValue(c) >= 0 && Character.getNumericValue(c) <= 6) {
                switch (Character.getNumericValue(c)) {
                    case 1:
                        addEmployee();
                        break;
                    case 2:
                        getAllEmployees();
                        break;
                    case 3:
                        getByName();
                        break;
                    case 4:
                        deleteByName();
                        break;
                    case 5:
                        increaseSalary();
                        break;
                    case 6:
                        getEmployeeWithSameSalary();
                        break;
                    case 0:
                        System.exit(0);
                }
            } else {
                System.out.println("Unsupported operation. Try again: ");
            }

            System.out.println("\nNext choice: ");
        }
    }

    public static void setConnectionAndCreateTable(String url, String name, String pass) {
        try {
            connection = DriverManager.getConnection(url, name, pass);
            if (connection != null) {
                System.out.println("\nConnected to DB");
            } else {
                System.out.println("Failed to connect to DB");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String fileURL) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new FileReader(new File(fileURL)));
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            Statement statement = connection.createStatement();
            statement.execute(stringBuilder.toString());

            initAddingEmpl("src/main/resources/init_insert.txt");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initAddingEmpl(String fileURL) throws FileNotFoundException, SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM employees");
        if (!resultSet.isBeforeFirst()) {
            Scanner scanner = new Scanner(new FileReader(new File(fileURL)));
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            statement.execute(stringBuilder.toString());
        }
    }


    public static void addEmployee() {
        String query = "INSERT INTO employees VALUES(?, ?, ?, ?, ?, ?)";
        Employee empl = new Employee();
        setEmployeeInfo(empl);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setLong(1, empl.getID());
            preparedStatement.setString(2, empl.getName());
            preparedStatement.setString(3, String.valueOf(empl.getSex()));
            preparedStatement.setDate(4, new Date(empl.getWorkDate().getTimeInMillis()));
            preparedStatement.setString(5, empl.getDepartment());
            preparedStatement.setDouble(6, empl.getSalary());
            if (preparedStatement.executeUpdate() != 0) {
                System.out.println("Employee " + empl.getName() + " added");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setEmployeeInfo(Employee employee) {
        System.out.println("ID: ");
        employee.setID(new Scanner(System.in).nextLong());

        System.out.println("Name: ");
        employee.setName(nameConverter(new Scanner(System.in).nextLine()));

        System.out.println("Sex(M or F): ");
        employee.setSex(new Scanner(System.in).nextLine().charAt(0));

        System.out.println("Date of start work(yyyy.mm.dd): ");
        employee.setWorkDate(new Scanner(System.in).nextLine());

        System.out.println("Department: ");
        employee.setDepartment(new Scanner(System.in).nextLine());

        System.out.println("Salary($): ");
        employee.setSalary(new Scanner(System.in).nextDouble());
    }

    public static void getAllEmployees() {
        String query = "SELECT * FROM employees ORDER BY department, workdate DESC";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            printResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void getByName() {
        String query = "SELECT * FROM employees WHERE name = ?";
        System.out.println("Enter the name of the employee you want to see: ");
        String name = nameConverter(new Scanner(System.in).nextLine());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                printResultSet(resultSet);
            } else {
                System.out.println("Employee with such name doesn't exist");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteByName() {
        String query = "DELETE FROM employees WHERE name = ?";
        System.out.println("Enter the name of the employee you want to delete: ");
        String name = nameConverter(new Scanner(System.in).nextLine());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            int row = preparedStatement.executeUpdate();
            System.out.println(row + " employee(s) deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void increaseSalary() {
        String query = "UPDATE employees SET salary = salary + ? WHERE id = ?";
        System.out.println("Enter the id of the employee you want to change salary: ");
        long id = new Scanner(System.in).nextInt();
        System.out.println("How much should be increased: ");
        double salary = new Scanner(System.in).nextDouble();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, salary);
            preparedStatement.setLong(2, id);
            System.out.println("Updated " + preparedStatement.executeUpdate() + " row(s)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void getEmployeeWithSameSalary() {
        String query =
                "SELECT name, salary FROM employees " +
                        "WHERE salary IN " +
                        "(SELECT salary FROM employees " +
                        "GROUP by salary " +
                        "HAVING count(*) > 1) " +
                        "ORDER BY salary DESC";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                System.out.print(resultSet.getString("name"));
                System.out.print("\t\t");
                System.out.print(resultSet.getString("salary"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String nameConverter(String name) {
        List<String> words = Arrays.asList(name.split(" "));
        words = words.
                stream().
                map((i) -> i = Character.toUpperCase(i.charAt(0)) + i.toLowerCase().substring(1)).
                collect(Collectors.toList());
        return String.join(" ", words);
    }

    private static void printResultSet(ResultSet resultSet) throws SQLException {
        if (!resultSet.isBeforeFirst()) {
            System.out.println("Table \"employees\" is empty");
        } else {
            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String name = resultSet.getString("name");
                char sex = resultSet.getString("sex").charAt(0);
                Date date = resultSet.getDate("workdate");
                String dep = resultSet.getString("department");
                double salary = resultSet.getDouble("salary");

                Employee employee = new Employee();
                employee.setID(id);
                employee.setName(name);
                employee.setSex(sex);
                employee.setWorkDate(date.toString());
                employee.setDepartment(dep);
                employee.setSalary(salary);

                System.out.println(employee);
            }
        }
    }


}