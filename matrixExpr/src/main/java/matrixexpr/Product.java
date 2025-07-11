package matrixexpr;

public class Product implements MatrixExpression {
    public Product(MatrixExpression m1, MatrixExpression m2) {
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
