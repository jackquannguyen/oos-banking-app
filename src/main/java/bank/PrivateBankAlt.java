package bank;

import bank.exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PrivateBankAlt implements Bank {
    /**
     *  der Name der privaten Bank
     */
    private String name;

    /**
     * die Zinsen bzw. der Zinssatz (positiver Wert von 0 bis 1) bei einer Einzahlung
     */
    private double incomingInterest;

    /**
     * die Zinsen bzw. der Zinssatz (positiver Wert von 0 bis 1) bei einer Auszahlung
     */
    private double outgoingInterest;
    /**
     * Knoten mit Transaktionen verknüpfen
     */
    protected Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    /**
     * der Konstruktor mit drei Attributen
     * @param newName der neue Name der Bank
     * @param newIncomingInterest der neue Zins bei der Einzahlung
     * @param newOutgoingInterest der neue Zins bei der AUszahlung
     */
    public PrivateBankAlt(String newName,double newIncomingInterest,double newOutgoingInterest){
        this.name = newName;
        this.incomingInterest = newIncomingInterest;
        this.outgoingInterest = newOutgoingInterest;
    }
    /**
     *  der Copy-Konstruktor
     * @param bank ist das zu kopierende Objekt
     */
    public PrivateBankAlt(PrivateBankAlt bank){
        this(bank.name, bank.incomingInterest, bank.outgoingInterest);
    }

    /**
     * @return den Name der privaten Bank
     */
    public String getName(){ return this.name;}
    /**
     * @return die Zinsen einer Einzahlung
     */
    public double getIncomingInterest(){ return this.incomingInterest;}
    /**
     * @return die Zinsen einer Auszahlung
     */
    public double getOutgoingInterest(){ return this.outgoingInterest;}

    /**
     * @param newName den neuen Namen für die Bank
     */
    public void setName(String newName){
        this.name = newName;
    }
    /**
     * @param newIncomingInterest legt ein neuer Zins bei der Einzahlung fest
     */
    public void setIncomingInterest(double newIncomingInterest){
        this.incomingInterest = newIncomingInterest;
    }

    /**
     * @param newOutgoingInterest legt ein neuer Zins bei der Auszahlung fest
     */
    public void setOutgoingInterest(double newOutgoingInterest){
        this.outgoingInterest = newOutgoingInterest;
    }

    /**
     * @return den Inhalt aller Klassenattribute
     */
    @Override
    public String toString() {
        return  "{PrivateBankAlt : "+ "name= " + name + ", incomingInterest= " + incomingInterest + ", outgoingInterest= " + outgoingInterest +'}';
    }

    /**
     * Zwei Objekte der Klasse PrivateBank zum Vergleich
     * @param obj das zu vergleichende Objekt
     * @return true, wenn beide sind gleich sonst false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PrivateBankAlt privateBankAlt){
            return (Objects.equals(this.name, privateBankAlt.name)) && (this.incomingInterest == privateBankAlt.incomingInterest)
                    && (this.outgoingInterest == privateBankAlt.outgoingInterest);
        }
        return false;
    }
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException {
        System.out.println("Creating new account " + account + " to bank " + name);
        if(accountsToTransactions.containsKey(account)){
            throw new AccountAlreadyExistsException("account <" + account +"> already exists!\n");
        }
        accountsToTransactions.put(account,List.of());
        System.out.println("Successful created new account");
    }

    @Override
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, TransferAmountException {
        System.out.println("Creating new account " + account + " to bank " + name);
        if(accountsToTransactions.containsKey(account) ||(accountsToTransactions.containsKey(account) && accountsToTransactions.containsValue(transactions))){
            throw new AccountAlreadyExistsException("account <" + account +"> already exists!\n");
        }
        if(true){
            for(int i = 0; i < transactions.size();i++){
                for(int y = i+1; y< transactions.size();y++)
                    if(transactions.get(i).equals(transactions.get(y))){
                        throw new TransactionAlreadyExistException("transactions already exist!\n");
                    }
            }
        }
        if(accountsToTransactions.containsKey(account) && !accountsToTransactions.containsValue(transactions)){
            for(Transaction transaction : transactions){
                if(!(transaction instanceof Payment)){
                    throw new TransactionAttributeException("Validation for transaction attributes: " + transaction + " fail!\n");
                }
            }
        }
        else{
            for(Transaction valueOfTransaction : transactions){
                if(valueOfTransaction instanceof  Payment payment){
                    payment.setIncomingInterest(this.incomingInterest);
                    payment.setOutgoingInterest(this.outgoingInterest);
                }
            }
            accountsToTransactions.put(account,transactions);
            System.out.println("Success!\n");
        }
    }

    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, TransferAmountException {
        List<Transaction> transactionList = new ArrayList<>(accountsToTransactions.get(account));
        if(!accountsToTransactions.containsKey(account)){
            throw new AccountDoesNotExistException("account <" + account + "> does not exist!\n");
        }
        if(accountsToTransactions.containsKey(account)){
            for(Transaction trans : transactionList){
                if(trans.equals(transaction)){
                    throw new TransactionAlreadyExistException("transaction <" + transaction + "> already exists!\n");
                }
            }
        }
        if(accountsToTransactions.containsKey(account) && !accountsToTransactions.containsValue(transaction)){
            if(!(transaction instanceof Payment)){
                throw new TransactionAttributeException("Validation for transaction attributes: " + transaction + " fail!\n");
            }
        }
        if(transaction instanceof Payment payment){
                payment.setOutgoingInterest(PrivateBankAlt.this.outgoingInterest);
                payment.setIncomingInterest(PrivateBankAlt.this.incomingInterest);
        }
        transactionList.add(transaction);
        accountsToTransactions.put(account,transactionList);
        System.out.println("Success to add new " + transaction + " to Postbank!\n");
    }

    @Override
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException, TransferAmountException {
        System.out.println("Removing transaction <" + transaction.toString().replace("\n", "") + "> from account <" + account + "> in bank <" + name + ">");
        if(!accountsToTransactions.containsKey(account)){
            throw new AccountDoesNotExistException("account <" + account + "> does not exist!\n");
        }
        if(!(accountsToTransactions.get(account).contains(transaction))){
            throw new TransactionDoesNotExistException("transaction <"+ transaction + "> does not exists!\n");
        }
        else{
            if(transaction instanceof Payment payment){
                payment.setOutgoingInterest(this.outgoingInterest);
                payment.setIncomingInterest(this.incomingInterest);
            }
            List<Transaction> transactionList = new ArrayList<>(accountsToTransactions.get(account));
            transactionList.remove(transaction);
            accountsToTransactions.put(account, transactionList);
            System.out.println("=> SUCCESS!\n");
        }
    }

    @Override
    public boolean containsTransaction(String account, Transaction transaction) throws TransferAmountException {
        if (transaction instanceof Payment payment) {
            payment.setIncomingInterest(PrivateBankAlt.this.incomingInterest);
            payment.setOutgoingInterest(PrivateBankAlt.this.outgoingInterest);
        }
        System.out.println("Checking account <" + account + "> contains the transaction <" +
                transaction.toString().
                        replace("\n", "") + "> : " + accountsToTransactions.get(account).contains(transaction) + "\n");
        return accountsToTransactions.get(account).contains(transaction) ;
    }

    @Override
    public double getAccountBalance(String account) {
        double balance = 0;
        for(Transaction transaction : accountsToTransactions.get(account)){
            if(transaction instanceof Transfer transfer && transfer.getSender().equals(account))
                balance = balance -  transaction.calculate();
            else
                balance = balance + transaction.calculate();
        }
        System.out.println("Balance of account <" + account + "> in bank <" + name + "> : " + balance + "\n");
        return balance;
    }

    @Override
    public List<Transaction> getTransactions(String account) {
        System.out.println("Transactions list of account <" + account + "> in bank <" + name + ">\n" +
                accountsToTransactions.get(account).toString().replace("[", "\t\t")
                        .replace("]","").replace("\n, ", "\n\t\t"));
        return accountsToTransactions.get(account);
    }

    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc){
        List<Transaction> sortedTransactionList = new ArrayList<>(accountsToTransactions.get(account));

        if(asc){
            sortedTransactionList.sort(Comparator.comparingDouble(Transaction::calculate));
            System.out.println("Sorting transactions of account <" + account + "> by calculated amounts in ASCENDING order:\n" +
                    sortedTransactionList.toString()
                            .replace("[", "\t\t")
                            .replace("]", "")
                            .replace("\n, ", "\n\t\t"));
        }else{
            sortedTransactionList.sort(Comparator.comparingDouble(Transaction::calculate).reversed());
            System.out.println("Sorting transactions of account <" + account + "> by calculated amounts in DESCENDING order:\n" +
                    sortedTransactionList.toString()
                            .replace("[", "\t\t")
                            .replace("]", "")
                            .replace("\n, ", "\n\t\t"));
        }
        return sortedTransactionList;
    }

    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactionListByType = new ArrayList<>();
        if (positive)
            System.out.println("List of POSITIVE transactions of account <" + account + "> :");
        else
            System.out.println("List of NEGATIVE transactions of account <" + account + "> :");
        for(Transaction transaction : accountsToTransactions.get(account)){
            if(positive && transaction.calculate() >= 0){
                transactionListByType.add(transaction);
            }
            else if(!positive && transaction.calculate() < 0){
                transactionListByType.add(transaction);
            }
        }
        System.out.println(transactionListByType.toString().replace("[", "\t\t").replace("]","").replace("\n, ", "\n\t\t"));
        return transactionListByType;
    }
    @Override
    public void deleteAccount(String account) throws AccountDoesNotExistException{
        if(!accountsToTransactions.containsValue(account)){
            throw new AccountDoesNotExistException("The account " + account + " doesn't exist");
        }
        accountsToTransactions.remove(account);
        System.out.println("Success deleting the account");
    }

    @Override
    public List<String> getAllAccounts() {
        List<String> allAccounts = new ArrayList<>(accountsToTransactions.keySet());
        return allAccounts;
    }

}
