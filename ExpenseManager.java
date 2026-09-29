
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;




public class ExpenseManager {
    private ArrayList<Expense> expenses;
    private double budget;

    public ExpenseManager(){
        this.expenses = new ArrayList<>();
    }
    
    public void addExpense(Expense expense){
        expenses.add(expense);
    }


    public ArrayList<Expense> getExpenses(){
        return expenses;
    }

    public double calculateTotal(){
        double total = 0;
        for(int i = 0; i < expenses.size(); i++){
            total += (expenses.get(i)).getAmountSpent();
        }
        return total;
    }

    public HashMap<ExpenseType, Double> calculateCategoryTotals(){
         HashMap<ExpenseType, Double> categories = new HashMap<>();
         for(int i = 0; i < expenses.size(); i++){
            ExpenseType type = expenses.get(i).getType();
            double amount = expenses.get(i).getAmountSpent();
            if(!categories.containsKey(type)){
                categories.put(type, amount);
            } else {
                categories.put(type, categories.get(type) + amount);
            }
         }
         return categories;
    }

    public void setBudget(double budget){
        this.budget = budget;
    }

    public double getBudget(){
        return budget;
    }

    public double calculateRemainingBudget(){
        return budget - calculateTotal();
    }

    public double calculateBudgetUsedPercentage(){
        if(budget == 0){
            return 0;
        }
        return (calculateTotal() / budget) * 100;
    }

    public String getBudgetStatus(){
        double percent = calculateBudgetUsedPercentage();
        if(percent > 100){
            return "Over Budget!";
        } else if(percent > 90){
            return "Near Budget.";
        } else {
            return "Within Budget";
        }
    }

    public Expense findLargestExpense(){
        if(expenses.isEmpty()){
            return null;
        }
        double highest = 0;
        int num = 0;
        for(int i = 0; i < expenses.size(); i++){
            if(expenses.get(i).getAmountSpent() > highest){
                highest = (expenses.get(i)).getAmountSpent();
                num = i;
            }
        }
        Expense largest = expenses.get(num);
        return largest;
    }

    public double calculateAverageExpense(){
        if(expenses.size() == 0){
            return 0;
        }
        return calculateTotal() / expenses.size();
    }

    public boolean deleteExpense(int index){
        if(index < 0 || index >= expenses.size()){
            return false;
        }
        expenses.remove(index);
        return true;
    }

    public boolean editExpense(int index, String store, double amountSpent, ExpenseType type, LocalDate date){
       if(index < 0 || index >= expenses.size()){
            return false;
       } 
       Expense newExpense = new Expense(store, amountSpent, type, date);
       expenses.set(index, newExpense);
       return true;
    }

    public void saveExpenses(){
        try {
            FileWriter writer = new FileWriter("expenses.txt");
            for(Expense expense: expenses){
                writer.write(expense.getStore() + "," + expense.getAmountSpent() + "," + expense.getType() + "," + expense.getDate() + "\n");
            }
            writer.close();
        } catch(Exception e){
            System.out.println("Error for saving expenses");
        }
    }

    public void loadExpenses() {
        try{
            Scanner fileScan = new Scanner(new File("expenses.txt"));
            expenses.clear();
            while(fileScan.hasNextLine()){
                String line = fileScan.nextLine();
                String[] parts = line.split(",");
                String store = parts[0];
                double amount = Double.parseDouble(parts[1]);
                ExpenseType type = ExpenseType.valueOf(parts[2]);
                LocalDate date = LocalDate.parse(parts[3]);
                Expense expense = new Expense(store, amount, type, date);
                expenses.add(expense);
            }
            fileScan.close();
        } catch (FileNotFoundException e){
            System.out.println("No Saved exceptions found.");
        }
    }

    public void exportToCSV(){
        try{
            FileWriter writer = new FileWriter("expenses.csv");
            writer.write("Store, Amount, Type, Date\n");

            for(Expense expense : expenses) {
                writer.write(expense.getStore() + "," + expense.getAmountSpent() + "," + expense.getType() + "," + expense.getDate() + "\n");            
            }
            writer.close();
        } catch (Exception e){
            System.out.println("Error exporting expenses");
        }
    }

    public Connection connect() {
    try {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:expenses.db");

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS expenses (" 
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " 
                + "store TEXT, "
                + "amount REAL, "
                + "type TEXT, "
                + "date TEXT)");
        }

        System.out.println("Connected to database!");
        return connection;

    } catch (SQLException e) {
        System.out.println("Database connection failed.");
        return null;
    }
}

    public void addExpenseToDatabase(Expense expense) {

        String sql = "INSERT INTO expenses (store, amount, type, date) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:expenses.db");
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1, expense.getStore());
            statement.setDouble(2, expense.getAmountSpent());
            statement.setString(3, expense.getType().toString());
            statement.setString(4, expense.getDate().toString());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding expense to database");
        }
    }

    public void loadExpensesFromDatabase() {

        String sql = "SELECT * FROM expenses";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:expenses.db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql)){

            expenses.clear();

            while (results.next()) {
                String store = results.getString("store");
                double amount = results.getDouble("amount");
                ExpenseType type = ExpenseType.valueOf(results.getString("type"));
                LocalDate date = LocalDate.parse(results.getString("date"));

                expenses.add(new Expense(store, amount, type, date));
            }


        } catch (SQLException e) {
            System.out.println("Error loading expenses from database.");
        }
    }

    public void updateExpenseInDatabase(int index, Expense expense) {

        String sql = "UPDATE expenses SET store = ?, amount = ?, type = ?, date = ? " +
                    "WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:expenses.db");
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1, expense.getStore());
            statement.setDouble(2, expense.getAmountSpent());
            statement.setString(3, expense.getType().toString());
            statement.setString(4, expense.getDate().toString());
            statement.setInt(5, index + 1);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating expense in database.");
        }
    }

    public void deleteExpenseFromDatabase(int index) {

        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:expenses.db");
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, index + 1);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting expense from database.");
        }
    }

}
