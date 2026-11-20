package bank;

import bank.exceptions.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;

public class PrivateBank implements Bank{
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
     * directory for all Json Files for PrivateBank
     */
    private String directoryName;

    /**
     * full path of directory where stores all files
     */
    private String fullPath;
    /**
     * Knoten mit Transaktionen verknüpfen
     */
    protected Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    /**
     * der Konstruktor mit drei Attributen
     * @param newName der neue Name der Bank
     * @param newDirectoryName das neue Verzeichnis
     * @param newIncomingInterest der neue Zins bei der Einzahlung
     * @param newOutgoingInterest der neue Zins bei der Auszahlung
     */
    public PrivateBank(String newName,String newDirectoryName,double newIncomingInterest,double newOutgoingInterest) throws AccountAlreadyExistsException {
        this.name = newName;
        this.directoryName = newDirectoryName;
        this.incomingInterest = newIncomingInterest;
        this.outgoingInterest = newOutgoingInterest;

       setFullPath(newDirectoryName, false);

        Path path = Paths.get(fullPath);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
                System.out.println("\nDirectory for " + PrivateBank.this.getName() + " is created!");
            } else {
                System.out.println("\nDirectory for " + PrivateBank.this.getName() + " is already exist!");
                System.out.println("=> Start reading account(s) from directory to " + PrivateBank.this.getName() + ":");
                readAccounts();
                System.out.println("FINISHED reading account(s) for " + PrivateBank.this.getName() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to create directory for " + PrivateBank.this.getName() + "!");
        }
    }
    /**
     *  der Copy-Konstruktor
     * @param bank ist das zu kopierende Objekt
     */
    public PrivateBank(PrivateBank bank) throws AccountAlreadyExistsException {
        this(bank.name, bank.directoryName, bank.incomingInterest, bank.outgoingInterest);
        this.accountsToTransactions = bank.accountsToTransactions;

        setFullPath(bank.directoryName,false);

        try {
            Path path = Paths.get(fullPath);

            if (Files.notExists(path)) {
                Files.createDirectories(path);
                System.out.println("\nDirectory for copied " + bank.getName() + " is created!");
            }
            else {
                System.out.println("\nDirectory for copied " + bank.getName() + " is already exist!");
                System.out.println("=> Start adding account(s) from directory to copied " + bank.getName() + ":");
                System.out.println("FINISHED reading account(s) for copied " + bank.getName() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to create directory for copied " + bank.getName() + "!");
        }
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
     *
     * @param directoryName name of the directory
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
    /**
     * @return a new directory name
     */
    public String getDirectoryName() {
        return directoryName;
    }

    public void setFullPath(String directoryName,boolean copiedBankPath){
        if(copiedBankPath)
            fullPath = "data/copiedPrivateBank/" + directoryName;
        else
            fullPath = "data/" + directoryName;

    }
    /**
     * Returns the Path to the Json elements
     * @return Path of the JsonElements
     */
    public String getFullPath(){
        return fullPath;
    }

    /**
     * @return den Inhalt aller Klassenattribute
     */
    @Override
    public String toString() {
        return  "{PrivateBank : "+ "name= " + name + ", incomingInterest= " + incomingInterest + ", outgoingInterest= " + outgoingInterest +'}';
    }

    /**
     * Zwei Objekte der Klasse PrivateBank zum Vergleich
     * @param obj das zu vergleichende Objekt
     * @return true, wenn beide sind gleich sonst false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PrivateBank privateBank){
            return (this.name.equals(privateBank.name)) && (this.incomingInterest == privateBank.incomingInterest)
                    && (this.outgoingInterest == privateBank.outgoingInterest);
        }
        return false;
    }


    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException {
        Path path = Path.of(PrivateBank.this.getFullPath() + "/" + account + ".json");

        if(Files.exists(path)){
            System.out.print("\nAdding " + account + " from the data system to " + name);
            if(accountsToTransactions.containsKey(account)){
                throw new AccountAlreadyExistsException("\naccount <" + account +"> already exists!\n");
            }
            accountsToTransactions.put(account,List.of());
            System.out.println("\nsuccessful created new account");
        }
        else{
            System.out.println("\nCreating new account: " + account + " to " + name);
            accountsToTransactions.put(account,List.of());
            writeAccount(account);
            System.out.println("\nsuccessful created new account");
        }
    }

    @Override
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, TransferAmountException, IOException {
        Path path = Path.of(PrivateBank.this.getFullPath() + "/" + account + ".json");

        if(Files.exists(path)) {
            System.out.print("\nAdding " + account + " from the data system to " + name + " with transactions list: ");
            System.out.println("Creating new account " + account + " to bank " + name);
            if(accountsToTransactions.containsKey(account)){
                throw new AccountAlreadyExistsException("account <" + account +"> already exists!\n");
            }
            for(int i = 0; i < transactions.size();i++){
                for(int y = i+1; y < transactions.size();y++)
                    if(transactions.get(i).equals(transactions.get(y))){
                        throw new TransactionAlreadyExistException("transactions already exist!\n");
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
        }else{
            System.out.print("\nCreating new account " + account + " to " + name + " with transactions list");
            for(Transaction valueOfTransaction : transactions){
                if(valueOfTransaction instanceof  Payment payment){
                    payment.setIncomingInterest(this.incomingInterest);
                    payment.setOutgoingInterest(this.outgoingInterest);
                }
            }
            accountsToTransactions.put(account,transactions);
            writeAccount(account);
            System.out.println("Success!\n");
        }
    }

    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, IOException, TransferAmountException {
        System.out.println("Adding new transaction <" + transaction.toString().replace("\n", "") + "> to account <" + account + "> in bank <" + name + ">");
        if (!accountsToTransactions.containsKey(account))
            throw new AccountDoesNotExistException("=> FAILED! ACCOUNT <" + account + "> DOES NOT EXISTS!\n");
        else {
            if (containsTransaction(account, transaction))
                throw new TransactionAlreadyExistException("=> FAILED! THIS TRANSACTION ALREADY EXISTS!\n");
            else {
                List<Transaction> transactionsList = new ArrayList<>(accountsToTransactions.get(account));
                transactionsList.add(transaction);
                accountsToTransactions.put(account, transactionsList);
                writeAccount(account);
                System.out.println("Success!\n");
            }

        }
    }

    @Override
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        System.out.println("Removing transaction <" + transaction.toString().replace("\n", "") + "> from account <" + account + "> in bank <" + name + ">");
        if(!accountsToTransactions.containsKey(account)){
            throw new AccountDoesNotExistException("account <" + account + "> does not exist!\n");
        }
        if(!(accountsToTransactions.get(account).contains(transaction))){
           throw new TransactionDoesNotExistException("transaction <"+ transaction + "> does not exists!\n");
        }
        else{
            List<Transaction> transactionList = new ArrayList<>(accountsToTransactions.get(account));
            transactionList.remove(transaction);
            accountsToTransactions.put(account, transactionList);
            writeAccount(account);
            System.out.println("Success!\n");
        }
    }

    @Override
    public boolean containsTransaction(String account, Transaction transaction) throws TransferAmountException {
        System.out.println("Checking account <" + account + "> contains the transaction <" +
                transaction.toString().
                replace("\n", "") + "> : " + accountsToTransactions.get(account).contains(transaction) + "\n");
        return accountsToTransactions.get(account).contains(transaction) ;
    }

    @Override
    public double getAccountBalance(String account) {
        double balance = 0;
        for (Transaction transaction : accountsToTransactions.get(account))
            balance = balance + transaction.calculate();
        System.out.println("Balance of account <" + account + "> in bank <" + name + "> : " + (double) Math.round(balance * 100) / 100 + "\n");
        return (double) Math.round(balance * 100) / 100;
    }

    @Override
    public List<Transaction> getTransactions(String account) {
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
        for(Transaction transaction : accountsToTransactions.get(account)){
            if(transaction.getAmount() > 0 && positive) transactionListByType.add(transaction);
            if(transaction.getAmount() <= 0 && !positive) transactionListByType.add(transaction);
        }
        return transactionListByType;
    }

    @Override
    public void deleteAccount(String account) throws AccountDoesNotExistException, IOException {
        if(!accountsToTransactions.containsKey(account)){
            throw new AccountDoesNotExistException("The account " + account + " doesn't exist");
        }
        accountsToTransactions.remove(account);
        Path path = Path.of(this.getFullPath() + "/" + account+".json");
        Files.deleteIfExists(path);
        System.out.println("Success deleting the account : " + account );
    }

    @Override
    public List<String> getAllAccounts() {
        return new ArrayList<>(accountsToTransactions.keySet());
    }

    /**
     * reads all available accounts from data system and provides them to the privatebank object
     * more precise: in class attribute accountsToTransactions
     */
    private void readAccounts() throws IOException, AccountAlreadyExistsException {
        final File folder = new File(PrivateBank.this.getFullPath());
        final File[] listOfFiles = Objects.requireNonNull(folder.listFiles());

        for(File file : listOfFiles){
            String accountName = file.getName().replace(".json","");
            PrivateBank.this.createAccount(accountName);

            try{
                Reader reader = new FileReader(PrivateBank.this.getFullPath() + "/" + file.getName());
                JsonArray parser = JsonParser.parseReader(reader).getAsJsonArray();

                for(JsonElement jsonElement : parser.getAsJsonArray()){

                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Gson gson = new GsonBuilder().registerTypeAdapter(Transaction.class,new TransactionAdapter()).create();

                    String str = gson.toJson(jsonObject.get("INSTANCE"));

                    if(jsonObject.get("CLASSNAME").getAsString().equals("Payment")){
                        Payment payment = gson.fromJson(str, Payment.class);
                        PrivateBank.this.addTransaction(accountName,payment);
                    }
                    else if(jsonObject.get("CLASSNAME").getAsString().equals("IncomingTransfer")){
                        IncomingTransfer incomingTransfer = gson.fromJson(str, IncomingTransfer.class);
                        PrivateBank.this.addTransaction(accountName,incomingTransfer);
                    }else{
                        OutgoingTransfer outgoingTransfer = gson.fromJson(str, OutgoingTransfer.class);
                        PrivateBank.this.addTransaction(accountName,outgoingTransfer);
                    }
                }

            }catch(IOException e){
                e.printStackTrace();
            } catch (TransactionAlreadyExistException | TransferAmountException | AccountDoesNotExistException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** persistent the given account in the data system (serialize and then store in Json )
     * @param account the account to be written
     */
    private void writeAccount(String account) throws  IOException{

        try(FileWriter fileWriter = new FileWriter(getFullPath()+"/"+ account + ".json")){
            fileWriter.write("[");
            for(Transaction transaction : accountsToTransactions.get(account)){
                Gson gson = new GsonBuilder()
                        .registerTypeHierarchyAdapter(transaction.getClass(), new TransactionAdapter())
                        .setPrettyPrinting()
                        .create();
                String json = gson.toJson(transaction);
                if(accountsToTransactions.get(account).indexOf(transaction) != 0){
                    fileWriter.write(",");
                }
                fileWriter.write(json);
            }

            fileWriter.write("]");
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
