
import java.time.LocalDate;

public class Expense {
    private String store;
    private double amountSpent;
    private ExpenseType type;
    private LocalDate date;

    public Expense(String store, double amountSpent, ExpenseType type, LocalDate date ){
        this.store = store;
        this.amountSpent = amountSpent;
        this.type = type;
        this.date = date;
    }

    public String getStore(){
        return store;
    }

    public double getAmountSpent(){
        return amountSpent;
    }

    public ExpenseType getType(){
        return type;
    }

    public LocalDate getDate(){
        return date;
    }

    public String toString(){
        return String.format(
            "Store: %s | Amount: $%.2f | Type: %s | Date: %s", 
             store, amountSpent, type, date);
    }

}
