
// Looked at doing LU Decomposition to find inverse
public class Matrix {

    private double[][] matrix;

    public Matrix(double[][] inputMat) {
        this.matrix = new double[inputMat.length][inputMat[0].length];
        // Do a deep copy of inputMat, so changes to inputMat do not affect this.matrix.
        for (int i = 0; i < inputMat.length; i++) {
            for (int j = 0; j < inputMat[0].length; j++) {
                this.matrix[i][j] = inputMat[i][j];
            }
        }
    }

    public Matrix add(Matrix other) {
        if (!this.hasSameDimensionAs(other)) {
            throw new IllegalArgumentException("Dimensions of matrices do not match.");
        }

        double[][] result = new double[this.matrix.length][this.matrix[0].length];

        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[0].length; j++) {
                result[i][j] = this.getAt(i, j) + other.getAt(i, j);
            }
        }
        return new Matrix(result);
    }

    // This matrix minus @param other
    public Matrix subtract(Matrix other) {
        Matrix negated = other.negate();
        return this.add(negated);
    }

    public Matrix scale(double scalar) {
        double[][] result = new double[this.matrix.length][this.matrix[0].length];
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[0].length; j++) {
                result[i][j] = this.getAt(i, j) * scalar;
            }
        }
        return new Matrix(result);
    }

    public Vector times(Vector vector) {
        double[] result = new double[this.matrix.length];

        for (int i = 0; i < this.matrix.length; i++) {
            double sum = 0;
            for (int j = 0; j < this.matrix[0].length; j++) {
                sum += this.getAt(i, j) * vector.get(j);
            }
            result[i] = sum;
        }
        return new Vector(result);
    }

    public Matrix times(Matrix other) {
        double[][] result = new double[this.matrix.length][other.matrix[0].length];
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < other.matrix[0].length; j++) {
                for (int k = 0; k < other.matrix.length; k++) {
                    result[i][j] += this.getAt(i, k) * other.getAt(k, j);
                }
            }
        }
        return new Matrix(result);
    }

    public Matrix invert() {
        int n = this.matrix.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i) {
            b[i][i] = 1;
        }

        // Transform the matrix into an upper triangle
        double[][] upperTrianglar = this.gaussian(this.matrix, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    b[index[j]][k] -= upperTrianglar[index[j]][i] * b[index[i]][k];
                }
            }
        }

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / this.matrix[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= upperTrianglar[index[j]][k] * x[k][i];
                }
                x[j][i] /= upperTrianglar[index[j]][j];
            }
        }
        return new Matrix(x);
    }

    // Method to carry out the partial-pivoting Gaussian
    // elimination. Here index[] stores pivoting order.
    public double[][] gaussian(double a[][], int index[]) {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i) {
            index[i] = i;
        }

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) {
                    c1 = c0;
                }
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[index[i]][j] / a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l) {
                    a[index[i]][l] -= pj * a[index[j]][l];
                }
            }
        }
        return a;
    }

    public Matrix transpose() {
        double[][] result = new double[this.matrix[0].length][this.matrix.length];
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[0].length; j++) {
                result[j][i] = this.getAt(i, j);
            }
        }
        return new Matrix(result);
    }

    public Matrix negate() {
        double[][] result = new double[this.matrix.length][this.matrix[0].length];
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[0].length; j++) {
                result[i][j] = -this.matrix[i][j];
            }
        }
        return new Matrix(result);
    }

    public boolean hasSameDimensionAs(Matrix other) {
        return this.matrix.length != other.matrix.length || this.matrix[0].length != other.matrix[0].length;
    }

    public double getAt(int i, int j) {
        return this.matrix[i][j];
    }

    public double[][] getMatrix() {
        return this.matrix;
    }

    public void setAt(int i, int j, double value) {
        this.matrix[i][j] = value;
    }

    public void setRow(int row, Vector input) {
        for (int i = 0; i < this.matrix[row].length; i++) {
            this.setAt(row, i, input.get(i));
        }
    }

    public int numDataPoints() {
        return this.matrix.length;
    }

    public Vector getDataPoint(int index) {
        return new Vector(this.matrix[index]);
    }

    public int numFeatures() {
        return this.matrix[0].length;
    }

    public boolean equals(Matrix other) {
        if (!this.hasSameDimensionAs(other)) {
            return false;
        }
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[0].length; j++) {
                if (this.getAt(i, j) != other.getAt(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void print() {
        for (int i = 0; i < this.numDataPoints(); i++) {
            for (int j = 0; j < this.numFeatures(); j++) {
                System.out.printf("%.2f", this.getAt(i, j));
                System.out.print(" ");
            }
            System.out.println("");
        }
        System.out.println("\n");
    }

    public void printTruncated() {
        if (this.numDataPoints() <= 10) {
            this.print();
            return;
        }
        for (int i = 0; i < 5; i++) { // Print first 5 rows
            for (int j = 0; j < this.numFeatures(); j++) {
                System.out.printf("%.2f", this.getAt(i, j));
                System.out.print(" ");
            }
            System.out.println("");
        }
        System.out.println("...");
        for (int i = this.numDataPoints() - 5; i < this.numDataPoints(); i++) { // Print last 5 rows
            for (int j = 0; j < this.numFeatures(); j++) {
                System.out.printf("%.2f", this.getAt(i, j));
                System.out.print(" ");
            }
            System.out.println("");
        }
        System.out.println("Dimensions: " + this.numDataPoints() + " x " + this.numFeatures() + "\n");
    }
}
