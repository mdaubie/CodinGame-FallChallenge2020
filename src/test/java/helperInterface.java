import java.util.ArrayList;

interface helperInterface {
    Logger LOGGER = new Logger();

    static int[] getNeededIngredientsFor(int[] delta) {
        int[] neededIngredients = new int[4];
        for (int i=0;i<delta.length;i++) {
            if (delta[i]<0) {neededIngredients[i]=-delta[i];}
        }
        return neededIngredients;
    }

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

    static int repeatableTimes(Action action) {
        return 10/sumIntArray(getNeededIngredientsFor(action.getDelta()));
    }

    static int[] multiplyIntArray(int[] array, int multiplier) {
        int[] newArray=new int[array.length];
        for (int i=0;i<array.length;i++) {
            newArray[i]=array[i]*multiplier;
        }
        return newArray;
    }

    static int valueOfDelta(int[] delta) {
        int value=0;
        for (int i=0;i<4;i++) {
            int ingValue=delta[i]*(i+1);
            value+=ingValue;
        }
        return value;
    }
}