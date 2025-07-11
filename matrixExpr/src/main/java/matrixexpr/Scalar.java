package matrixexpr;

public class Scalar implements MatrixExpression {

    public Scalar(double value) {
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
