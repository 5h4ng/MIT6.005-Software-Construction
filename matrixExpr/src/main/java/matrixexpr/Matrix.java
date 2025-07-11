package matrixexpr;

public class Matrix implements MatrixExpression {
    public Matrix(double[][] array) {
    }

    @Override
    public boolean isIdentity() {
        return false;
    }

    @Override
    public MatrixExpression scalars() {
        return null;
    }

    @Override
    public MatrixExpression matrices() {
        return null;
    }

    @Override
    public MatrixExpression optimize() {
        return null;
    }
}
