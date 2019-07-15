package mr.go.sgfilter;

import static org.junit.Assert.assertEquals;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.Linearizer;
import mr.go.sgfilter.MeanValuePadder;
import mr.go.sgfilter.RamerDouglasPeuckerFilter;
import mr.go.sgfilter.SGFilter;
import mr.go.sgfilter.ZeroEliminator;

import org.junit.Test;

public class FilterTestCase {

  private void assertCoeffsEqual(double[] coeffs, double[] tabularCoeffs) {
    for (int i = 0; i < tabularCoeffs.length; i++) {
      assertEquals(tabularCoeffs[i], coeffs[i], 0.001);
    }
  }

  private void assertResultsEqual(double[] results, double[] real) {
    for (int i = 0; i < real.length; i++) {
      assertEquals(real[i], results[i], 0.001);
    }
  }

  private void assertResultsEqual(float[] results, double[] real) {
    for (int i = 0; i < real.length; i++) {
      assertEquals(real[i], results[i], 0.1);
    }
  }

  @Test
  public final void testComputeSGCoefficients() {
    System.out.print("Testing testComputeSGCoefficients()…");
    double[] coeffs = SGFilter.computeSGCoefficients(5, 5, 2);
    double[] tabularCoeffs = new double[]{
      -0.084,
       0.021,
       0.103,
       0.161,
       0.196,
       0.207,
       0.196,
       0.161,
       0.103,
       0.021,
      -0.084
    };
    assertEquals(11, coeffs.length);
    assertCoeffsEqual(coeffs, tabularCoeffs);
    coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
    tabularCoeffs = new double[]{
       0.042,
      -0.105,
      -0.023,
       0.140,
       0.280,
       0.333,
       0.280,
       0.140,
      -0.023,
      -0.105,
       0.042};
    assertEquals(11, coeffs.length);
    assertCoeffsEqual(coeffs, tabularCoeffs);
    coeffs = SGFilter.computeSGCoefficients(4, 0, 2);
    tabularCoeffs = new double[]{
       0.086,
      -0.143,
      -0.086,
       0.257,
       0.886};
    assertEquals(5, coeffs.length);
    assertCoeffsEqual(coeffs, tabularCoeffs);
    System.out.println("success.");
  }

  @Test
  public final void testDouglasPeuckerFilter() {
    System.out.print("Testing testDouglasPeuckerFilter()…");
    double[] data = new double[]{
      2.9,
      1.3,
      1.5,
      1.6,
      1.6,
      1.0,
      1.5,
      2.0,
      1.5,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0,
      1.0};
    double[] result = new RamerDouglasPeuckerFilter(0.5).filter(data);
    double[] real = new double[]{
      2.9,
      1.3,
      1.6,
      1.0,
      2.0,
      1.0,
      1.0};
    assertResultsEqual(result, real);
    System.out.println("success.");
  }

  @Test
  public final void testMeanValuePadderLeft() {
    System.out.print("Testing testMeanValuePadderLeft()…");
    double[] data = new double[]{
      0, 0, 0, 0, 0,
      8915.06,
      8845.53,
      9064.17,
      8942.09,
      8780.87,
      8916.81,
      8934.24,
      9027.06,
      9160.79,
      7509.14};
    double[] real = new double[]{
      8909.544000000002,
      8909.544000000002,
      8909.544000000002,
      8909.544000000002,
      8909.544000000002,
      8915.06,
      8845.53,
      9064.17,
      8942.09,
      8780.87,
      8916.81,
      8934.24,
      9027.06,
      9160.79,
      7509.14};
    new MeanValuePadder(10, true, false).apply(data);
    assertResultsEqual(data, real);
    System.out.println("success.");
  }

  @Test
  public final void testLinearizer() {
    System.out.print("Testing testLinearizer()…");
    double[] data = new double[]{
      6945.43,
         0.00,
         0.00,
      7221.76,
      4092.77,
      6607.28,
      6867.01};
    double[] real = new double[]{
      6945.43,
         0.00,
         0.00,
      2202.426666666667,
      4404.853333333333,
      6607.280000000001,
      6867.01};
    new Linearizer(0.08f).apply(data);
    assertResultsEqual(data, real);
    System.out.println("success.");
  }

  @Test
  public final void testMeanValuePadderRight() {
    System.out.print("Testing testMeanValuePadderRight()…");
    double[] data = new double[]{
      8915.06,
      8845.53,
      9064.17,
      8942.09,
      8780.87,
      8916.81,
      8934.24,
      9027.06,
      9160.79,
      7509.14,
      0, 0, 0, 0};
    double[] real = new double[]{
      8915.06,
      8845.53,
      9064.17,
      8942.09,
      8780.87,
      8916.81,
      8934.24,
      9027.06,
      9160.79,
      7509.14,
      8709.608,
      8709.608,
      8709.608,
      8709.608};
    new MeanValuePadder(10, false, true).apply(data);
    assertResultsEqual(data, real);
    System.out.println("success.");
  }

  @Test
  public final void testSmooth() {
    System.out.print("Testing testSmooth()…");
    float[] data = new float[]{
      8.91f,
      8.84f,
      9.06f,
      8.94f,
      8.78f};
    float[] leftPad = new float[]{
      8.91f,
      8.93f,
      9.02f,
      9.16f,
      7.50f};
    double[] realResult = new double[]{
      8.56394,
      8.740239999999998,
      8.962772,
      9.077350000000001,
      8.80455};
    double[] coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
    ContinuousPadder padder1 = new ContinuousPadder(false, true);
    SGFilter sgFilter = new SGFilter(5, 5);
    sgFilter.appendPreprocessor(padder1);
    float[] smooth = sgFilter.smooth(data, leftPad, new float[0], coeffs);
    assertResultsEqual(smooth, realResult);
    System.out.println("success.");
  }


  // scipy.signal.savgol_filter([2, 2.2, 2, 1.8, 2, 2.2, 2], 5, 2, mode="nearest")
  @Test
  public final void testSciPySG() {
    System.out.print("Testing testsSciPySG()…");
    float[] data = new float[]{
      2.0f,
      2.2f,
      2.0f,
      1.8f,
      2.0f,
      2.2f,
      2.0f};
    double[] coeffs = SGFilter.computeSGCoefficients(2, 2, 2);
    float[] pad = new float[]{
      2.0f,
      2.0f,
    };
    double[] realResult = new double[]{
      2.06857143,
      2.11428571,
      2.00000000,
      1.86857143,
      2.00000000,
      2.11428571,
      2.06857143};
    SGFilter sgFilter = new SGFilter(2, 2);
    float[] smooth = sgFilter.smooth(data, pad, pad, coeffs);
    assertResultsEqual(smooth, realResult);
    System.out.println("success.");
  }

  @Test
  public final void testSmoothWithBias() {
    System.out.print("Testing testSmoothWithBias()…");
    double[] coeffs5_5 = SGFilter.computeSGCoefficients(5, 5, 4);
    double[] coeffs5_4 = SGFilter.computeSGCoefficients(5, 4, 4);
    double[] coeffs4_5 = SGFilter.computeSGCoefficients(4, 5, 4);
    float[] data = new float[]{
      1.26f,
      1.83f,
      1.83f,
      1.83f,
      1.83f,
      1.81f,
      1.81f,
      1.88f,
      1.88f,
      1.84f,
      1.84f,
      1.84f,
      1.84f};
    double[] real = new double[]{
      1.79390,
      1.80085,
      1.83971,
      1.85462,
      1.84520};
    SGFilter sgFilter = new SGFilter(5, 5);
    float[] smooth = sgFilter.smooth(data, 4, 9, 1, new double[][]{coeffs5_5, coeffs5_4, coeffs4_5});
    assertResultsEqual(smooth, real);
    System.out.println("success.");
  }
}
