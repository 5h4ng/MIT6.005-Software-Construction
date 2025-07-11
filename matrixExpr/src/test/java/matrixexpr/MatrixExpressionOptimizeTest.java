package matrixexpr;

import org.junit.Test;
import static org.junit.Assert.*;

public class MatrixExpressionOptimizeTest {

    // Helper
    private static void assertExprEquals(MatrixExpression expected, MatrixExpression actual) {
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void testOptimize_ZeroScalars() {
        // X ⇒ X  covers 0 scalars
        MatrixExpression X = MatrixExpression.make(new double[][]{{1, 2}, {3, 4}});
        assertExprEquals(X, X.optimize());
    }

    @Test
    public void testOptimize_OneScalarImmediateLeft() {
        // aX ⇒ aX  1 scalar, immediate left
        MatrixExpression a = MatrixExpression.make(5.0);
        MatrixExpression X = MatrixExpression.make(new double[][]{{1, 2}, {3, 4}});
        MatrixExpression aX = MatrixExpression.times(a, X);
        assertExprEquals(aX, aX.optimize());
    }

    @Test
    public void testOptimize_TwoScalars_ImmediateLeft_RightOfRight() {
        // a(Xb) ⇒ (ab)X  2 scalars, immediate left, right-of-right
        MatrixExpression a = MatrixExpression.make(2.0);
        MatrixExpression b = MatrixExpression.make(3.0);
        MatrixExpression X = MatrixExpression.make(new double[][]{{1, 2}, {3, 4}});
        MatrixExpression Xb = MatrixExpression.times(X, b);
        MatrixExpression a_Xb = MatrixExpression.times(a, Xb);
        MatrixExpression abX = MatrixExpression.times(MatrixExpression.make(6.0), X);
        assertExprEquals(abX, a_Xb.optimize());
    }

    @Test
    public void testOptimize_TwoScalars_ImmediateRight_LeftOfLeft() {
        // (aX)b ⇒ (ab)X  2 scalars, immediate right, left-of-left
        MatrixExpression a = MatrixExpression.make(2.0);
        MatrixExpression b = MatrixExpression.make(3.0);
        MatrixExpression X = MatrixExpression.make(new double[][]{{1, 2}, {3, 4}});
        MatrixExpression aX = MatrixExpression.times(a, X);
        MatrixExpression aX_b = MatrixExpression.times(aX, b);
        MatrixExpression abX = MatrixExpression.times(MatrixExpression.make(6.0), X);
        assertExprEquals(abX, aX_b.optimize());
    }

    @Test
    public void testOptimize_TwoScalars_LeftOfRight_RightOfLeft() {
        // (Xa)(bY) ⇒ (((ab)X)Y) 2 scalars, left-of-right, right-of-left
        MatrixExpression a = MatrixExpression.make(2.0);
        MatrixExpression b = MatrixExpression.make(3.0);
        MatrixExpression X = MatrixExpression.make(new double[][]{{1, 2}, {3, 4}});
        MatrixExpression Y = MatrixExpression.make(new double[][]{{5, 6}, {7, 8}});
        MatrixExpression Xa = MatrixExpression.times(X, a);
        MatrixExpression bY = MatrixExpression.times(b, Y);
        MatrixExpression prod = MatrixExpression.times(Xa, bY);
        // (((ab)X)Y)
        MatrixExpression ab = MatrixExpression.make(6.0);
        MatrixExpression abX = MatrixExpression.times(ab, X);
        MatrixExpression expected = MatrixExpression.times(abX, Y);
        assertExprEquals(expected, prod.optimize());
    }

    @Test
    public void testOptimize_MoreThanTwoScalars() {
        // ((aX)b)c ⇒ (abc)X   >2 scalars, left, right, nested
        MatrixExpression a = MatrixExpression.make(2.0);
        MatrixExpression b = MatrixExpression.make(3.0);
        MatrixExpression c = MatrixExpression.make(4.0);
        MatrixExpression X = MatrixExpression.make(new double[][]{{1, 2}, {3, 4}});
        MatrixExpression aX = MatrixExpression.times(a, X);
        MatrixExpression aXb = MatrixExpression.times(aX, b);
        MatrixExpression prod = MatrixExpression.times(aXb, c);
        MatrixExpression abcX = MatrixExpression.times(MatrixExpression.make(24.0), X);
        assertExprEquals(abcX, prod.optimize());
    }
}