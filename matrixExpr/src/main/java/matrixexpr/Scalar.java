package matrixexpr;

public class Scalar implements MatrixExpression {
    final double value;

    public Scalar(double value) {
        this.value = value;
    }

    @Override
    public boolean isIdentity() {
        return value == 1;
    }

    @Override
    public MatrixExpression scalars() {
        return this;
    }

    @Override
    public MatrixExpression matrices() {
        return I;
    }

    @Override
    public MatrixExpression optimize() {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
