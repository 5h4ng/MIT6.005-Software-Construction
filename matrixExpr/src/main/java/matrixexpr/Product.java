package matrixexpr;

public class Product implements MatrixExpression {
    final MatrixExpression m1;
    final MatrixExpression m2;
    //  RI:
    //      m1's column count == m2's row count or m1 is scalar or m2 is scalar


    public Product(MatrixExpression m1, MatrixExpression m2) {
        this.m1 = m1;
        this.m2 = m2;
    }

    @Override
    public boolean isIdentity() {
        return m1.isIdentity() && m2.isIdentity();
    }

    @Override
    public MatrixExpression scalars() {
        return MatrixExpression.times(m1.scalars(), m2.scalars());
    }

    @Override
    public MatrixExpression matrices() {
        return MatrixExpression.times(m1.matrices(), m2.matrices());
    }

    @Override
    public MatrixExpression optimize() {
        return MatrixExpression.times(scalars(), matrices());
    }

    @Override
    public String toString() {
        return "(" + m1 + " * " + m2 + ")";
    }
}
