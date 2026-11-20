package bank;

import bank.exceptions.TransferAmountException;

public class Transfer extends Transaction {
    /**
     * Name des Absenders, der die Überweisung gemacht hat
     */
    private String sender;
    /**
     * Name des Empfängers, der die Überweisung bekommen hat
     */

    private String recipient;

    /**
     * Konstruktor mit drei Attributen
     * @param newDate Wert für date
     * @param newAmount Wert für amount
     * @param newDescription Wert für description
     */

    public Transfer(String newDate,String newDescription,double newAmount) throws TransferAmountException {
        super(newDate,newDescription,newAmount);
        if(newAmount < 0.0){
            throw new TransferAmountException("The amount is negativ!\n");
        }
    }

    /**
     * Konstruktor mit drei Attributen
     * @param newDate Wert für date
     * @param newDescription Wert für description
     * @param newAmount Wert für amount
     * @param newSender Wert für sender
     * @param newRecipient Wert für recipient
     */

    public Transfer(String newDate,String newDescription, double newAmount,String newSender,String newRecipient) throws TransferAmountException {
        super(newDate,newDescription,newAmount);
        if(newAmount < 0.0){
            throw new TransferAmountException("The amount is negativ!\n");
        }
        this.sender = newSender;
        this.recipient = newRecipient;
    }

    /**
     * Copy Constructor
     * @param newTransfer neue Objekt festzulegen
     */

    public Transfer(Transfer newTransfer) throws TransferAmountException {
        this(newTransfer.date, newTransfer.description, newTransfer.amount, newTransfer.sender, newTransfer.recipient);
    }
    /**
     * @return den aktuellen Wert von sender
     */

    public String getSender() {
        return sender;
    }
    /**
     * Legt das Attribut sender fest
     * @param newSender neuer Wert für sender
     */

    public void setSender(String newSender) {
        this.sender = newSender;
    }
    /**
     * @return den aktuellen Wert von recipient
     */
    public String getRecipient() {
        return recipient;
    }
    /**
     * Legt das Attribut recipient fest
     * @param newRecipient neuer Wert von recipient
     */

    public void setRecipient(String newRecipient) {
        this.recipient = newRecipient;
    }
    /**
     * @return den Inhalt aller Klassenattribute
     */
    @Override
    public String toString() {
        return super.toString() + "sender=" + sender + ", recipient=" + recipient + '}';
    }

    /**
     * @return unveränderter Wert von amount
     */
    @Override
    public double calculate() {
        return this.amount;
    }
    /**
     * Zwei Objekte der Klasse Transfer zum Vergleich
     * @param obj das zu vergleichende Objekt
     * @return true, wenn beide sind gleich sonst false
     */

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transfer transfer)
            return (super.equals(transfer) && this.sender.equals(transfer.sender) && this.recipient.equals(transfer.recipient));
        return false;
    }
}

