package OpenCraft.World.Item;

public class Item {

    private int stackAmount;
    private int amount;

    public Item(int stackAmount, int amount) {
        this.stackAmount = stackAmount;
        this.amount = amount;
    }

    public int getStackAmount() {
        return stackAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setStackAmount(int stackAmount) {
        this.stackAmount = stackAmount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
