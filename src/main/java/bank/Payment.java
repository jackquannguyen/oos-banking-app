package bank;

import bank.exceptions.TransferAmountException;

public class Payment extends Transaction {
    /**
     * die Zinsen (positiver Wert in Prozent, 0 bis 1) bei einer Einzahlung anfallen
     */
    private double incomingInterest;
    /**
     * die Zinsen (positiver Wert in Prozent, 0 bis 1) bei einer Auszahlung anfallen
     */
    private double outgoingInterest;

    /**
     * Konstruktor mit drei Attributen
     * @param newDate Wert für date
     * @param newDescription Wert für description
     * @param newAmount Wert für amount
     */
    public Payment(String newDate,String newDescription,double newAmount){
        super(newDate,newDescription,newAmount);
    }



    /**
     * Konstruktor mit aller Attribute
     * @param newDate Wert für date
     * @param newDescription Wert für description
     * @param newAmount Wert für amount
     * @param newIncomingInterest Wert für incomingInterest
     * @param newOutgoingInterest Wert für outgoingInterest
     */
    public Payment(String newDate,String newDescription, double newAmount,double newIncomingInterest,double newOutgoingInterest ) throws TransferAmountException {
        this(newDate, newDescription,newAmount);
        setIncomingInterest(newIncomingInterest);
        setOutgoingInterest(newOutgoingInterest);
    }
    /**
     * Copy Constructor
     * @param newPayment neue Objekt festzulegen
     */
    public Payment(Payment newPayment) throws TransferAmountException {
        this(newPayment.date,newPayment.description,newPayment.amount,newPayment.incomingInterest,newPayment.outgoingInterest);
    }

    /**
     * @return den aktuellen Wert von incomingInterest
     */

    public double getIncomingInterest() {
        return incomingInterest;
    }
    /**
     * Legt das Attribut newIncomingInterest fest
     * @param newIncomingInterest neuer Wert für newIncomingInterest
     */
    public void setIncomingInterest(double newIncomingInterest) throws TransferAmountException {
        if(newIncomingInterest < 0.0 || newIncomingInterest > 1.0){
            throw new TransferAmountException("Incoming Interest is invalid!\n");
        }else{
            this.incomingInterest = newIncomingInterest;
        }
    }
    /**
     * @return den aktuellen Wert von outgoingInterest
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }
    /**
     * Legt das Attribut outgoingInterest fest
     * @param newOutgoingInterest neuer Wert für outgoingInterest
     */
    public void setOutgoingInterest(double newOutgoingInterest) throws TransferAmountException {
        if(newOutgoingInterest < 0.0 || newOutgoingInterest > 1.0){
            throw new TransferAmountException("Invalid outgoing Interest - Withdrawal");
        }else{
            this.outgoingInterest = newOutgoingInterest;
        }
    }
    /**
     * @return neue amount nach Interest
     */
    @Override
    public double calculate() {
        if(getAmount() >= 0){
            return amount - (amount * incomingInterest);
        }else{
            return amount + (amount * incomingInterest);
        }
    }
    /**
     * @return den Inhalt aller Klassenattribute
     */
    @Override
    public String toString() {
        return super.toString() + "incomingInterest= " + incomingInterest + ", outgoingInterest= " + outgoingInterest +'}';
    }

    /**
     * Zwei Objekte der Klasse Payment zum Vergleich
     * @param obj das zu vergleichende Objekt
     * @return true, wenn beide sind geich sonst false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Payment payment){
            return (super.equals(payment) && incomingInterest == payment.incomingInterest && outgoingInterest == payment.outgoingInterest);
        }
        return false;
    }
}































