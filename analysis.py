import sqlite3
import pandas as pd
import matplotlib.pyplot as plt

connection = sqlite3.connect("expenses.db")

df = pd.read_sql_query("SELECT * FROM expenses", connection)


print("Total spending:", df["amount"].sum())
print("Average expense:", df["amount"].mean())

print("\nSpending by category:")
print(df.groupby("type")["amount"].sum())

category_totals = df.groupby("type")["amount"].sum()
category_totals.plot(kind="bar")

plt.title("Spending by Category")
plt.xlabel("Category")
plt.ylabel("Amount ($)")
plt.tight_layout()
plt.show()

df["date"] = pd.to_datetime(df["date"])
daily_spending = df.groupby("date")["amount"].sum()
daily_spending.plot(kind="line", marker="o")

plt.title("Spending Over Time")
plt.xlabel("Date")
plt.ylabel("Amount ($)")
plt.tight_layout()
plt.show()

connection.close()