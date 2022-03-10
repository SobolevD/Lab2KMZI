package org.example.utils;

import java.util.List;

public abstract class Matrices {

    private static void swap(List<List<Integer>> mat, int row1, int row2, int col) {
        for (int i = 0; i < col; i++) {
            int temp = mat.get(row1).get(i);
            mat.get(row1).set(i, mat.get(row2).get(i));
            mat.get(row2).set(i, temp);
        }
    }

    public static int rankOfMatrix(List<List<Integer>> mat) {

        int c;
        int r = c = mat.size();

        int rank = c;
        for (int row = 0; row < rank; row++) {

            if (mat.get(row).get(row) != 0) {
                for (int col = 0; col < r; col++) {
                    if (col != row) {
                        double mult = (double) mat.get(col).get(row) / mat.get(row).get(row);
                        for (int i = 0; i < rank; i++)
                            mat.get(col).set(i, mat.get(col).get(i) - (int)mult * mat.get(row).get(i));
                    }
                }
            }
            else {
                boolean reduce = true;
                for (int i = row + 1; i < r; i++) {
                    if (mat.get(i).get(row) != 0) {
                        swap(mat, row, i, rank);
                        reduce = false;
                        break;
                    }
                }
                if (reduce) {
                    rank--;
                    for (int i = 0; i < r; i++)
                        mat.get(i).set(row, mat.get(i).get(rank));
                }
                row--;
            }
        }
        return rank;
    }
}
