import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BudgetTrackerApp extends JFrame {

    private Map<Date, Transaction> transactions = new HashMap<>();
    private double budgetGoal = 0;
    private JTextField incomeNameField, incomeDescriptionField, incomeAmountField;
    private JTextField expenseNameField, expenseDescriptionField, expenseAmountField;
    private JTextField budgetGoalField;
    private JButton addIncomeButton, addExpenseButton, clearTransactionsButton, addBudgetGoalButton;
    private JLabel balanceLabel, budgetStatusLabel, budgetGoalLabel;
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private DecimalFormat df = new DecimalFormat("0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public BudgetTrackerApp() {
        super("Budget Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //User Interface Components
        //Income Component
        JPanel incomePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        incomePanel.setBorder(BorderFactory.createTitledBorder("Income"));
        incomeNameField = new JTextField();
        incomeDescriptionField = new JTextField();
        incomeAmountField = new JTextField();
        addIncomeButton = new JButton("Add Income");
        incomePanel.add(new JLabel("Name:"));
        incomePanel.add(incomeNameField);
        incomePanel.add(new JLabel("Description:"));
        incomePanel.add(incomeDescriptionField);
        incomePanel.add(new JLabel("Amount:"));
        incomePanel.add(incomeAmountField);
        incomePanel.add(new JLabel());
        incomePanel.add(addIncomeButton);

        //Expense Component
        JPanel expensePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        expensePanel.setBorder(BorderFactory.createTitledBorder("Expense"));
        expenseNameField = new JTextField();
        expenseDescriptionField = new JTextField();
        expenseAmountField = new JTextField();
        addExpenseButton = new JButton("Add Expense");
        expensePanel.add(new JLabel("Name:"));
        expensePanel.add(expenseNameField);
        expensePanel.add(new JLabel("Description:"));
        expensePanel.add(expenseDescriptionField);
        expensePanel.add(new JLabel("Amount:"));
        expensePanel.add(expenseAmountField);
        expensePanel.add(new JLabel());
        expensePanel.add(addExpenseButton);

        //Budget Component
        JPanel budgetPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        budgetPanel.setBorder(BorderFactory.createTitledBorder("Budget"));
        budgetGoalField = new JTextField();
        addBudgetGoalButton = new JButton("Set Budget Goal");
        budgetPanel.add(new JLabel("Budget Goal:"));
        budgetPanel.add(budgetGoalField);
        budgetPanel.add(new JLabel());
        budgetPanel.add(addBudgetGoalButton);

       //Info Component
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        balanceLabel = new JLabel("Balance: N0.00");
        budgetStatusLabel = new JLabel("Budget Status: N/A");
        budgetGoalLabel = new JLabel("Budget Goal: N0.00");
        infoPanel.add(balanceLabel);
        infoPanel.add(budgetStatusLabel);
        infoPanel.add(budgetGoalLabel);

        JPanel controlPanel = new JPanel(new GridLayout(1,1));
        controlPanel.setBorder(BorderFactory.createTitledBorder("clear transaction"));
        clearTransactionsButton = new JButton("Clear Transactions");
        controlPanel.add(clearTransactionsButton);

        //Transaction Table
        String[] columnNames = {"Date", "Type", "Name", "Description", "Amount"};
        transactionTableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);

        //Adding components to the JFrame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(incomePanel, BorderLayout.WEST);
        getContentPane().add(expensePanel, BorderLayout.EAST);
        getContentPane().add(budgetPanel, BorderLayout.NORTH);
        getContentPane().add(controlPanel, BorderLayout.AFTER_LAST_LINE);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(infoPanel, BorderLayout.PAGE_END);


        //Setting up Action Listeners
        addIncomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction("Income");
            }
        });

        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction("Expense");
            }
        });

        clearTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTransactions();
            }
        });

        addBudgetGoalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBudgetGoal();
            }
        });


        //Initial User Interface Updates
        updateBalance();
        updateBudgetStatus();

        setVisible(true);
    }


    //Methods
    private void addTransaction(String type) {
        try {
            Date date = new Date();
            String name = (type.equals("Income") ? incomeNameField.getText() : expenseNameField.getText());
            String description = (type.equals("Income") ? incomeDescriptionField.getText() : expenseDescriptionField.getText());
            double amount = Double.parseDouble((type.equals("Income") ? incomeAmountField.getText() : expenseAmountField.getText()));

            Transaction transaction = new Transaction(date, type, name, description, amount);
            transactions.put(date, transaction);
            updateTransactionList();
            updateBalance();
            updateBudgetStatus();

            //Clear input fields
            if (type.equals("Income")) {
                incomeNameField.setText("");
                incomeDescriptionField.setText("");
                incomeAmountField.setText("");
            } else {
                expenseNameField.setText("");
                expenseDescriptionField.setText("");
                expenseAmountField.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void clearTransactions() {
        transactions.clear();
        updateTransactionList();
        updateBalance();
        updateBudgetStatus();
    }

    private void setBudgetGoal() {
        try {
            budgetGoal = Double.parseDouble(budgetGoalField.getText());
            updateBudgetStatus();
            budgetGoalField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "You entered an invalid budget goal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBalance() {
        double balance = 0;
        for (Transaction transaction : transactions.values()) {
            if (transaction.getType().equals("Income")) {
                balance += transaction.getAmount();
            } else {
                balance -= transaction.getAmount();
            }
        }
        balanceLabel.setText("Balance: N" + df.format(balance));
    }

    private void updateBudgetStatus() {
        double expenses = 0;
        for (Transaction transaction : transactions.values()) {
            if (transaction.getType().equals("Expense")) {
                expenses += transaction.getAmount();
            }
        }
        if (budgetGoal == 0) {
            budgetStatusLabel.setText("No Budget Goal");
        } else if (expenses <= budgetGoal) {
            budgetStatusLabel.setText("Budget Status: On Track");
        } else {
            budgetStatusLabel.setText("Budget Status: Over Budget");
        }

        budgetGoalLabel.setText("Budget Goal: N" + df.format(budgetGoal));
    }

    private void updateTransactionList() {
        transactionTableModel.setRowCount(0);
        for (Transaction transaction : transactions.values()) {
            transactionTableModel.addRow(new Object[]{
                    dateFormat.format(transaction.getDate()),
                    transaction.getType(),
                    transaction.getName(),
                    transaction.getDescription(),
                    df.format(transaction.getAmount())
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BudgetTrackerApp());
    }
}

class Transaction {
    private Date date;
    private String type;
    private String name;
    private String description;
    private double amount;

    public Transaction(Date date, String type, String name, String description, double amount) {
        this.date = date;
        this.type = type;
        this.name = name;
        this.description = description;
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }
    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public double getAmount() {
        return amount;
    }

}