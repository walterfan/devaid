package com.github.walterfan.util.algorithm;

import lombok.extern.slf4j.Slf4j;

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

    public static <T> void insertSort(T[] arr, Comparator<T> comparator) {
        //Iterables.toArray(source, String.class);
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


        void merge(int arr[], int l, int m, int r)
        {
            // Find sizes of two subarrays to be merged
            int n1 = m - l + 1;
            int n2 = r - m;

            /* Create temp arrays */
            int L[] = new int [n1];
            int R[] = new int [n2];

            /*Copy data to temp arrays*/
            for (int i=0; i<n1; ++i)
                L[i] = arr[l + i];
            for (int j=0; j<n2; ++j)
                R[j] = arr[m + 1+ j];


            /* Merge the temp arrays */

            // Initial indexes of first and second subarrays
            int i = 0, j = 0;

            // Initial index of merged subarry array
            int k = l;
            while (i < n1 && j < n2)
            {
                if (L[i] <= R[j])
                {
                    arr[k] = L[i];
                    i++;
                }
                else
                {
                    arr[k] = R[j];
                    j++;
                }
                k++;
            }

            /* Copy remaining elements of L[] if any */
            while (i < n1)
            {
                arr[k] = L[i];
                i++;
                k++;
            }

            /* Copy remaining elements of R[] if any */
            while (j < n2)
            {
                arr[k] = R[j];
                j++;
                k++;
            }
        }

        // Main function that sorts arr[l..r] using
        // merge()
        void mergeSort(int arr[], int l, int r)
        {
            if (l < r)
            {
                // Find the middle point
                int m = (l+r)/2;

                // Sort first and second halves
                mergeSort(arr, l, m);
                mergeSort(arr , m+1, r);

                // Merge the sorted halves
                merge(arr, l, m, r);
            }
        }


    }
