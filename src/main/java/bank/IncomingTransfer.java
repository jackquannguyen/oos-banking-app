package bank;

import bank.exceptions.TransferAmountException;

public class IncomingTransfer extends Transfer{

    /**
     * Konstruktor mit drei Attributen
     *
     * @param newDate        Wert für date
     * @param newDescription Wert für description
     * @param newAmount      Wert für amount
     */
    public IncomingTransfer(String newDate, String newDescription, double newAmount) throws TransferAmountException {
        super(newDate, newDescription, newAmount);
    }

    /**
     * Konstruktor mit drei Attributen
     *
     * @param newDate        Wert für date
     * @param newDescription Wert für description
     * @param newAmount      Wert für amount
     * @param newSender      Wert für sender
     * @param newRecipient   Wert für recipient
     */
    public IncomingTransfer(String newDate, String newDescription, double newAmount, String newSender, String newRecipient) throws TransferAmountException {
        super(newDate, newDescription, newAmount, newSender, newRecipient);
    }

    /**
     * Copy Constructor
     *
     * @param newTransfer neue Objekt festzulegen
     */
    public IncomingTransfer(Transfer newTransfer) throws TransferAmountException {
        super(newTransfer);
    }

    @Override
    public double calculate() {
        return amount;
    }


}
