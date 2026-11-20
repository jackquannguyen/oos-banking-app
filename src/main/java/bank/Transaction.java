package bank;

public abstract class Transaction implements CalculateBill {
    /**
     * stellt den Zeitpunkt der Transaktion mit dem Format als DD.MM.YYYY dar
     */
    protected String date;
    /**
     * stellt die Geldmenge der Transaktion dar
     */
    protected double amount;
    /**
     * eine zusätzliche Beschreibung des Vorgangs
     */
    protected String description;
    /**
     * Konstruktor für abstrakte Klasse
     * @param newDate Wert für date
     * @param newDescription Wert für description
     * @param newAmount Wert für amount
     */

    public Transaction(String newDate,String newDescription, double newAmount) {
        this.date = newDate;
        this.description = newDescription;
        setAmount(newAmount);
    }

    /**
     * @return den aktuellen Wert von date
     */

    public String getDate() {
        return date;
    }

    /**
     * Legt das Attribut date fest
     * @param newDate neuer Wert für date
     */
    public void setDate(String newDate) {
        this.date = newDate;
    }

    /**
     * @return den aktuellen Wert von amount
     */
    public double getAmount() {
        return amount;
    }
    /**
     * Legt das Attribut date fest
     * @param newAmount neuer Wert für date
     */

    public void setAmount(double newAmount) {
        this.amount = newAmount;
    }
    /**
     * @return den aktuellen Wert von description
     */

    public String getDescription() {
        return description;
    }
    /**
     * Legt das Attribute description fest
     * @param newDescription neuer Wert für description
     */

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * @return den Inhalt aller Klassenattribute
     */

    @Override
    public String toString() {
        return "Transaction{" + " date= " + date  + ",description= " + description + ",amount= " + amount +",";
    }

    /**
     * Zwei Transaktionsobjekte zum Vergleich
     * @param obj das zu vergleichende Objekt
     * @return true, wenn beide sind gleich sonst false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transaction transaction) {
            return (this.date.equals(transaction.date) && this.amount == transaction.amount && this.description.equals(transaction.description));
        }
        return false;
    }
}
