import java.util.ArrayList;

interface helperInterface {
    Logger LOGGER = new Logger();

    static int[] copyIntArray(int[] originalArray) {
        int length = originalArray.length;
        int[] newArray = new int[length];
        System.arraycopy(originalArray, 0, newArray, 0, length);
        return newArray;
    }

    static int sumIntArray(int[] intArray) {
        int sum=0;
        for (int value : intArray) { sum += value; }
        return sum;
    }

    static ArrayList<Action> copyActionArray(ArrayList<Action> originalArray) {
        ArrayList<Action> newArray = new ArrayList<>();
        for(Action action : originalArray) {
            newArray.add(new Action(action));
        }
        return newArray;
    }
}