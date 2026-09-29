import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.time.LocalDate;


public class Main {

   public static void main(String[] args) {
      ExpenseManager manager = new ExpenseManager();
      manager.connect();
      manager.loadExpensesFromDatabase();

      Scanner scanner = new Scanner(System.in);

      System.out.println("Expense Tracker");
     
      int option = -1;
      while(option != 10){
         System.out.println();
         System.out.println("1. Add Expense ");
         System.out.println("2. View Expenses ");
         System.out.println("3. View Total ");
         System.out.println("4. View Spending by Category ");
         System.out.println("5. Set Budget ");
         System.out.println("6. View Budget ");
         System.out.println("7. Delete Expense ");
         System.out.println("8. Edit Expense ");
         System.out.println("9. Export to CSV");
         System.out.println("10. Exit ");

         boolean validOption = false;

         while(!validOption){
            System.out.print("Please enter a number: ");
            try {
              option = Integer.parseInt(scanner.nextLine());
              if(option >= 1 && option <= 10){
                  validOption = true;
              } else {
                  System.out.println("Invalid. out of range! Try again 1-9: ");
              }
              
            } catch (NumberFormatException e){
               System.out.println("Please enter a valid number");
            }
         }

         System.out.println();
         if(option == 1){
            System.out.print("Store :");
            String store = getValidStore(scanner);

            System.out.print("Amount: ");
            double amountSpent = getValidAmount(scanner);
            

            System.out.println("Type: ");
            System.out.println("1. Food");
            System.out.println("2. Clothing");
            System.out.println("3. Gas ");
            System.out.println("4. Shopping ");
            System.out.println("5. Entertainment");
            System.out.println("6. Other");
            System.out.print("Enter Type: ");
            
            ExpenseType type = getValidType(scanner);

            System.out.print("Date (YYYY-MM-DD): ");
            LocalDate date = null;
            boolean validDate = false;
            while(!validDate){
               try {
                  date = LocalDate.parse(scanner.nextLine());
                  validDate = true;
               } catch (Exception e){
                  System.out.println("Invalid date");
                  System.out.println("Try again, Enter date: ");
               }
            }

            Expense expense = new Expense(store, amountSpent, type, date);
            manager.addExpense(expense);
            manager.addExpenseToDatabase(expense);
            System.out.println("Expense added!");
         } else if(option == 2){
            ArrayList<Expense> expenses = manager.getExpenses();
            if(expenses.isEmpty()){
               System.out.println("No expenses recorded yet.");
            }
            for(int i = 0; i < expenses.size(); i++){
               System.out.println((i+1)+ ". " + expenses.get(i));
            }
         } else if(option == 3){
            System.out.print("Total: $" + String.format("%.2f", manager.calculateTotal()));         } else if(option == 4){
            HashMap<ExpenseType, Double> categoryTotals = manager.calculateCategoryTotals();
            for(ExpenseType category : categoryTotals.keySet()){
               System.out.println(category + ": $" + String.format("%.2f", categoryTotals.get(category)));
            }
         } else if(option == 5){
            System.out.print("Enter Budget: ");

            double budget = -1;
            boolean validBudget = false;
            while(!validBudget){
               try {
                  budget = Double.parseDouble(scanner.nextLine());
                  if(budget <= 0){
                     System.out.println("Invalid budget: cannot be negative or zero"); 
                     System.out.print("Enter amount: ");
                  } else {
                     validBudget = true;
                  }
               } catch (NumberFormatException e){
                  System.out.println("Try again, Enter budget: ");
               }
            }
            manager.setBudget(budget);

         } else if(option == 6){
            System.out.println("========= Budget Dashboard ==========");
            System.out.println("Budget: $" + String.format("%.2f", manager.getBudget()));
            System.out.println("Spent: $" + String.format("%.2f", manager.calculateTotal()));
            System.out.println("Remaining Budget: $" + String.format("%.2f", manager.calculateRemainingBudget()));
            System.out.println("Used: " + String.format("%.2f", manager.calculateBudgetUsedPercentage()) + "%");
            System.out.println("Status: " + manager.getBudgetStatus());
            System.out.println();
            System.out.println("Largest Expense: ");
            System.out.println(manager.findLargestExpense());
            System.out.println("Average Expense: ");
            System.out.println(manager.calculateAverageExpense());
            System.out.println("=====================================");
         } else if(option == 7){
            System.out.print("Enter the expense number to delete: ");
            int index = -1;

            boolean validIndex = false;

            while(!validIndex){  
               try {
                  index = Integer.parseInt(scanner.nextLine());
                  validIndex = true;
               } catch (NumberFormatException e){
                  System.out.println("Please enter a valid number");
                  System.out.print("Enter the expense number to delete: ");
               }
            }
            

            if(manager.deleteExpense(index - 1)){
               manager.deleteExpenseFromDatabase(index - 1);
               System.out.println("Expense deleted!");
            } else {
               System.out.println("Invalid expense number!");
            }
         } else if(option == 8){
            System.out.print("Enter the expense number to edit: ");
            int index = -1;

            boolean validIndex = false;

            while(!validIndex){  
               try {
                  index = Integer.parseInt(scanner.nextLine());
                  validIndex = true;
               } catch (NumberFormatException e){
                  System.out.println("Please enter a valid number");
                  System.out.print("Enter the expense number to edit: ");
               }
            }

            System.out.println("Now enter for the new expense that you want you replace");
            System.out.print("Store :");
            String store = getValidStore(scanner);


            System.out.print("Amount: ");
            double amountSpent = getValidAmount(scanner);

            System.out.println("Type: ");
            System.out.println("1. Food");
            System.out.println("2. Clothing");
            System.out.println("3. Gas ");
            System.out.println("4. Shopping ");
            System.out.println("5. Entertainment");
            System.out.println("6. Other");
            System.out.print("Enter Type: ");
            
            
            ExpenseType type = getValidType(scanner);

            System.out.print("Date (YYYY-MM-DD): ");
            LocalDate date = null;
            boolean validDate = false;
            while(!validDate){
               try {
                  date = LocalDate.parse(scanner.nextLine());
                  validDate = true;
               } catch (Exception e){
                  System.out.println("Invalid date");
               }
            }

            if(manager.editExpense(index - 1, store, amountSpent, type, date)){
               Expense editedExpense = manager.getExpenses().get(index - 1);
               manager.updateExpenseInDatabase(index - 1, editedExpense);
               System.out.println("Expense replaced!");
            } else {
               System.out.println("Invalid expense number!");
            }

         } else if(option == 9){
            manager.exportToCSV();
         }
      }
   }

   public static double getValidAmount(Scanner scanner){
      double amountSpent = -1;
      boolean validAmount = false;

      while(!validAmount){
         try {
            amountSpent = Double.parseDouble(scanner.nextLine());
            if(amountSpent <= 0){
               System.out.println("Invalid amount: cannot be negative or zero"); 
                System.out.println("Enter amount: ");
            } else {
               validAmount = true;
            }
         } catch (NumberFormatException e){
            System.out.println("Try again, Enter amount: ");
         }
      }
      return amountSpent;
   }

   public static String getValidStore(Scanner scanner){
      String store = "";

      boolean validName = false;

      while(!validName){
         store = scanner.nextLine();
         if(store.trim().isEmpty()){
            System.out.println("Invalid name: Please add name of the store"); 
            System.out.print("Enter store: ");
         } else {
            validName = true;
         }
      }
      return store;
   }

   public static ExpenseType getValidType(Scanner scanner){
      int typeChoice = -1;
      boolean validNum = false;
      while(!validNum){
         try {
            typeChoice = Integer.parseInt(scanner.nextLine());
            if(typeChoice >= 1 && typeChoice <= 6){
               validNum = true;
            } else {
               System.out.println("Invalid choice");
               System.out.println("Try again, Enter choice: ");
            }
         } catch (NumberFormatException e){
            System.out.println("Try again, Enter choice: ");
         }
      }
      return ExpenseType.values()[typeChoice - 1];
   }
}
