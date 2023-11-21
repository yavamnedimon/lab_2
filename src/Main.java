import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        int[] array = {5, 3, 8, 4, 2, 1, 7, 6};
        System.out.println("Original array: " + Arrays.toString(array));

        parallelMergeSort(array);

        System.out.println("Sorted array: " + Arrays.toString(array));

    }
    private static class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;

        public MergeSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (low < high) {
                int mid = (low + high) >>> 1;

                MergeSortTask leftTask = new MergeSortTask(array, low, mid);
                MergeSortTask rightTask = new MergeSortTask(array, mid + 1, high);

                invokeAll(leftTask, rightTask);

                merge(array, low, mid, high);
            }
        }

        private void merge(int[] array, int low, int mid, int high) {
            int leftLength = mid - low + 1;
            int rightLength = high - mid;

            int[] leftArray = Arrays.copyOfRange(array, low, low + leftLength);
            int[] rightArray = Arrays.copyOfRange(array, mid + 1, mid + 1 + rightLength);

            int i = 0, j = 0, k = low;

            while (i < leftLength && j < rightLength) {
                if (leftArray[i] <= rightArray[j]) {
                    array[k++] = leftArray[i++];
                } else {
                    array[k++] = rightArray[j++];
                }
            }

            while (i < leftLength) {
                array[k++] = leftArray[i++];
            }

            while (j < rightLength) {
                array[k++] = rightArray[j++];
            }
        }
    }

    public static void parallelMergeSort(int[] array) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new MergeSortTask(array, 0, array.length - 1));
        forkJoinPool.shutdown();
    }
}