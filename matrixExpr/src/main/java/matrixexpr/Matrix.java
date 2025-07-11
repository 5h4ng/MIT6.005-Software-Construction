package matrixexpr;

public class Matrix implements MatrixExpression {
    final double[][] array;
    // RI: array.length > 0 and array[i] are all equal nonzero length

    public Matrix(double[][] array) {
        this.array = array; // note: danger!
    }

    @Override
    public boolean isIdentity() {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                double expected = (i == j) ? 1 : 0;
                if (array[i][j] != expected) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public MatrixExpression scalars() {
        return I;
    }

    @Override
    public MatrixExpression matrices() {
        return this;
    }

    @Override
    public MatrixExpression optimize() {
        return this;
    }

    @Override
    public String toString() {
        return "[" + array.length + "x" + array[0].length + "]";
    }
}
