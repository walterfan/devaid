package com.github.walterfan.util.algorithm;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class SortUtils {

    private static <T> void swap(T o1, T o2) {
        T temp = o1;
        o1 = o2;
        o2 = temp;
    }



    public static <T> void bubbleSort(T[] arr, Comparator<T> comparator) {
        boolean sorted = false;
        int loopCount = arr.length - 1;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < loopCount; i++) {
                if (comparator.compare(arr[i], arr[i + 1]) > 0) {
                    swap(arr[i], arr[i + 1]);
                    sorted = false;
                }
            }
        }
    }

    public static <T> void bubbleSort(List<T> aList, Comparator<T> comparator) {
        boolean sorted = false;
        int loopCount = aList.size() - 1;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < loopCount; i++) {
                if (comparator.compare(aList.get(i), aList.get(i + 1)) > 0) {
                    Collections.swap(aList, i, i + 1);
                    sorted = false;
                }
            }
        }
    }

    public static <T> void insertSort(List<T> aList, Comparator<T> comparator) {
        int size = aList.size();
        for (int i = 1; i < size; ++i) {
            T selected = aList.get(i);

            if (size < 10) {
                log.info("{} insert to {}", selected, aList.subList(0, i).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }

            int j = i - 1;
            //find a position for insert currentElement in the left sorted collection
            while (j >= 0 && comparator.compare(selected, aList.get(j)) < 0) {
                //it does not overwrite existed element because the j+1=i that is currentElement at beginging
                aList.set(j + 1, aList.get(j));
                j--;
            }
            aList.set(j + 1, selected);

        }
    }


    public static <T> int binarySearch(T a[], T item, Comparator<T> comparator, int low, int high) {
        if (high <= low)
            return (comparator.compare(item, a[low]) > 0) ? (low + 1) : low;

        int mid = (low + high) / 2;

        if (comparator.compare(item, a[mid]) == 0)
            return mid + 1;

        if (comparator.compare(item, a[mid]) > 0)
            return binarySearch(a, item, comparator, mid + 1, high);
        return binarySearch(a, item, comparator, low, mid - 1);
    }

    // Function to sort an array a[] of size 'n'
    public static <T> void insertionSort(T a[], int n, Comparator<T> comparator) {
        int i, loc, j, k;

        for (i = 1; i < n; ++i) {
            j = i - 1;
            T selected = a[i];

            // find location where selected sould be inseretd
            loc = binarySearch(a, selected, comparator, 0, j);

            // Move all elements after location to create space
            while (j >= loc) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = selected;
        }
    }

    public static <T> void mergeSort(List<T> aList, Comparator<T> comparator) {

        mergeSort((T[])aList.toArray(), 0, aList.size() -1, comparator);
    }

    public static <T> void quickSort(List<T> aList, Comparator<T> comparator) {
        quickSort((T[])aList.toArray(), 0, aList.size() -1, comparator);

    }
       public static<T> void merge(T arr[], int l, int m, int r, Comparator<T> comparator)
        {
            // Find sizes of two subarrays to be merged
            int n1 = m - l + 1;
            int n2 = r - m;

            /* Create temp arrays */
            List<T> L = new ArrayList<T>(n1);
            List<T> R = new ArrayList<T>(n2);

            /*Copy data to temp arrays*/
            for (int i=0; i<n1; ++i)
                L.set(i, arr[l + i]);
            for (int j=0; j<n2; ++j)
                R.set(j, arr[m + 1+ j]);


            /* Merge the temp arrays */

            // Initial indexes of first and second subarrays
            int i = 0, j = 0;

            // Initial index of merged subarry array
            int k = l;
            while (i < n1 && j < n2)
            {
                if (comparator.compare(L.get(i), R.get(j)) <=0)
                {
                    arr[k] = L.get(i);
                    i++;
                }
                else
                {
                    arr[k] = R.get(j);
                    j++;
                }
                k++;
            }

            /* Copy remaining elements of L[] if any */
            while (i < n1)
            {
                arr[k] = L.get(i);
                i++;
                k++;
            }

            /* Copy remaining elements of R[] if any */
            while (j < n2)
            {
                arr[k] = R.get(j);
                j++;
                k++;
            }
        }

        // Main function that sorts arr[l..r] using
        // merge()
        public static <T> void mergeSort(T arr[], int l, int r, Comparator<T> comparator)
        {
            if (l < r)
            {
                // Find the middle point
                int m = (l+r)/2;

                // Sort first and second halves
                mergeSort(arr, l, m, comparator);
                mergeSort(arr , m+1, r, comparator);

                // Merge the sorted halves
                merge(arr, l, m, r, comparator);
            }
        }



    public static <T> int partition(T arr[], int low, int high, Comparator<T> comparator)
    {
        T pivot = arr[high];
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            if (comparator.compare(arr[j] , pivot) < 0)
            {
                i++;

                // swap arr[i] and arr[j]
                swap(arr[i], arr[j]);
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        swap(arr[i+1], arr[high]);
        return i+1;
    }

    public static <T> void quickSort(T arr[], int low, int high, Comparator<T> comparator)
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, low, high, comparator);

            // Recursively sort elements before
            // partition and after partition
            quickSort(arr, low, pi-1, comparator);
            quickSort(arr, pi+1, high, comparator);
        }
    }
}
