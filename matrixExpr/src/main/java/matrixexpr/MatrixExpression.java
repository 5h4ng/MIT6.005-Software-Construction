package matrixexpr;

/** Represents an immutable expression of matrix and scalar products.
 *  <a href="https://github.com/mit6005/S14-L14-programming-with-adts/tree/master">Official Version</a>
 */
public interface MatrixExpression {
    /**
     * @return true if this is the identity
     */
    boolean isIdentity();

    /**
     * @return the product of all scalar factors as a MatrixExpression
     */
    MatrixExpression scalars();

    /** @return the product of all the matrices in this expression.
     * times(scalars(), matrices()) is equivalent to this expression.
     */
    MatrixExpression matrices();

    /**
     * @return an optimized MatrixExpression
     */
    MatrixExpression optimize();

    /**
     * Returns the product of two matrix expressions.
     * @param m1 the left operand
     * @param m2 the right operand
     * @return the product expression
     * @throws IllegalArgumentException if the two expressions are not compatible for multiplication
     */
    static MatrixExpression times(MatrixExpression m1, MatrixExpression m2) {
        if (m1.isIdentity()) return m2;
        else if (m2.isIdentity()) return m1;
        else return new Product(m1, m2);
    }

    /**
     * Factory method: creates a scalar expression.
     * @return a matrix expression consisting of just the scalar value
     */
    static MatrixExpression make(double value) {
        return new Scalar(value);
    }

    /**
     * Factory method: creates a matrix expression.
     * @param array a non-empty 2D array with equal non-zero-length rows
     * @return a MatrixExpression representing the matrix
     */
    static MatrixExpression make(double[][] array) {
        return new Matrix(array);
    }

    static final MatrixExpression I = new Identity();
}
