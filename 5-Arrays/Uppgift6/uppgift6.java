import java.util.Arrays;

public class uppgift6 {
    public static void main(String[] args) {
        int[] K = new int[10];

        for (int i = 0; i < K.length; i++) {
            K[i] = i + 1;
        }

        System.out.println("Original array: " + Arrays.toString(K));

        for (int i = 0; i < K.length - 1; i++) {
            for (int j = 0; j < K.length - i - 1; j++) {
                if (K[j] > K[j + 1]) {
                    int temp = K[j];
                    K[j] = K[j + 1];
                    K[j + 1] = temp;
                }
            }
        }

        System.out.println("Sorted array: " + Arrays.toString(K));
    }
}
