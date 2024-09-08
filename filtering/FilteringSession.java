import ij.*;

public class FilteringSession {

	/*******************************************************************************
	 *
	 * E D G E   D E T E C T O R   S E C T I O N
	 *
	 ******************************************************************************/

	/**
	 * Detects the vertical edges inside an ImageAccess object.
	 * This is the non-separable version of the edge detector.
	 * The kernel of the filter has the following form:
	 *
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *
	 * Mirror border conditions are applied.
	 */
	static public ImageAccess detectEdgeVertical_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[3][3];
		double pixel;
		ImageAccess out = new ImageAccess(nx, ny);
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				pixel = arr[2][0]+arr[2][1]+arr[2][2]-arr[0][0]-arr[0][1]-arr[0][2];
				pixel = pixel / 6.0;
				out.putPixel(x, y, pixel);
			}
		}
		out = rescale(out);
		return out;
	}

	/**
	 * Detects the vertical edges inside an ImageAccess object.
	 * This is the separable version of the edge detector.
	 * The kernel of the filter applied to the rows has the following form:
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *
	 * The kernel of the filter applied to the columns has the following 
	 * form:
	 *     -------
	 *     |  1  |
	 *     -------
	 *     |  1  |
	 *     -------
	 *     |  1  |
	 *     -------
	 *
	 * Mirror border conditions are applied.
	 */
	static public ImageAccess detectEdgeVertical_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			input.getRow(y, rowin);
			doDifference3(rowin, rowout);
			out.putRow(y, rowout);
		}
		
		double colin[]  = new double[ny];
		double colout[] = new double[ny];
		for (int x = 0; x < nx; x++) {
			out.getColumn(x, colin);
			doAverage3(colin, colout);
			out.putColumn(x, colout);
		}
		out = rescale(out);
		return out;
	}

		/**
	 * Detects the horizontal edges inside an ImageAccess object.
	 * This is the non-separable version of the edge detector.
	 * The kernel of the filter has the following form:
	 *
	 *     -------------------
	 *     | -1  | -1  | -1  |
	 *     -------------------
	 *     |  0  |  0  |  0  |
	 *     -------------------
	 *     |  1  |  1  |  1  |
	 *     -------------------
	 *
	 * Mirror border conditions are applied.
	 */

	static public ImageAccess detectEdgeHorizontal_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[3][3];
		double pixel;
		ImageAccess out = new ImageAccess(nx, ny);
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				pixel = arr[2][2]+arr[1][2]+arr[0][2]-arr[0][0]-arr[1][0]-arr[2][0];
				pixel = pixel / 6.0;
				out.putPixel(x, y, pixel);
			}
		}
		out = rescale(out);
		return out;
	}

	/**
	 * Detects the Horizontal edges inside an ImageAccess object.
	 * This is the separable version of the edge detector.
	 * The kernel of the filter applied to the rows has the following form:
	 *     -------------------
	 *     |  1  |  1  |  1  |
	 *     -------------------
	 *
	 * The kernel of the filter applied to the columns has the following 
	 * form:
	 *     -------
	 *     | -1  |
	 *     -------
	 *     |  0  |
	 *     -------
	 *     |  1  |
	 *     -------
	 *
	 * Mirror border conditions are applied.
	 */

	static public ImageAccess detectEdgeHorizontal_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);

		double colin[]  = new double[ny];
		double colout[] = new double[ny];
		for (int x = 0; x < nx; x++) {
			input.getColumn(x, colin);
			doDifference3(colin, colout);
			out.putColumn(x, colout);
		}

		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			out.getRow(y, rowin);
			doAverage3(rowin, rowout);
			out.putRow(y, rowout);
		}
		out = rescale(out);
		return out;
	}

	/**
	 * Implements an one-dimensional average filter of length 3.
	 * The filtered value of a pixel is the averaged value of
	 * its local neighborhood of length 3.
	 * Mirror border conditions are applied.
	 */
	static private void doAverage3(double vin[], double vout[]) {
		int n = vin.length;
		vout[0] = (vin[0] + 2.0 * vin[1]) / 3.0;
		for (int k = 1; k < n-1; k++) {
			vout[k] = (vin[k-1] + vin[k] + vin[k+1]) / 3.0;
		}
		vout[n-1] = (vin[n-1] + 2.0 * vin[n-2]) / 3.0;
	}

	/**
	 * Implements an one-dimensional centered difference filter of 
	 * length 3. The filtered value of a pixel is the difference of 
	 * its two neighborhing values.
	 * Mirror border conditions are applied.
	 */
	static private void doDifference3(double vin[], double vout[]) {
		int n = vin.length;
		vout[0] = 0.0;
		for (int k = 1; k < n-1; k++) {
			vout[k] = (vin[k+1] - vin[k-1]) / 2.0;
		}
		vout[n-1] = 0.0;
	}

	/*******************************************************************************
	 *
	 * M O V I N G   A V E R A G E   5 * 5   S E C T I O N
	 *
	 ******************************************************************************/

	static public ImageAccess doMovingAverage5_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[5][5];
		double pixel = 0;
		ImageAccess out = new ImageAccess(nx, ny);
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				
				for(int i = 0 ; i < 5; i++){
					for(int j = 0 ; j < 5; j++){
						pixel = pixel + arr[i][j];
				}}
				pixel = pixel / 25.0;
				out.putPixel(x, y, pixel);
			}
		}
		out = rescale(out);
		return out;
	}

	static public ImageAccess doMovingAverage5_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);

		double colin[]  = new double[ny];
		double colout[] = new double[ny];

		for (int x = 0; x < nx; x++) {
			input.getColumn(x, colin);
			
			Average5(colin, colout);

			out.putColumn(x, colout);
		}

		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			out.getRow(y, rowin);
			Average5(rowin, rowout);
			out.putRow(y, rowout);
		}
		out = rescale(out);
		return out;
	}

	static private void Average5(double vin[], double vout[]){

		int n = vin.length;

		vout[0] = (vin[0] + 2.0 * vin[1] + 2.0 * vin[2]) / 5.0;
		vout[1] = (vin[0] + vin[1] + vin[2] + 2.0 *vin[3]) / 5.0;

		for (int k = 2; k < n-2; k++) {
			vout[k] = (vin[k-2] + vin[k-1] + vin[k] + vin[k+1] + vin[k+2]) / 5.0;
		}
		vout[n-2] = (vin[n-1] + vin[n-2] + vin[n-3] + 2 * vin[n-4]) / 5.0;
		vout[n-1] = (vin[n-1] + 2.0 * vin[n-2] + 2.0 * vin[n-3]) / 5.0;
		
	}

	static public ImageAccess doMovingAverage5_Recursive(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
	
		double colin[]  = new double[ny];
		double colout[] = new double[ny];
	
		for (int x = 0; x < nx; x++) {
			input.getColumn(x, colin);
			recursiveAverage5(colin, colout, 0);
			out.putColumn(x, colout);
		}
	
		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			out.getRow(y, rowin);
			recursiveAverage5(rowin, rowout, 0);
			out.putRow(y, rowout);
		}
		out = rescale(out);
		return out;
	}

	static private void recursiveAverage5(double vin[], double vout[], int k) {
		int n = vin.length;
	
		if (k == 0) {
			vout[0] = (vin[0] + 2.0 * vin[1] + 2.0 * vin[2]) / 5.0;
			recursiveAverage5(vin, vout, k + 1);
		} else if (k == 1) {
			vout[1] = (vin[0] + vin[1] + vin[2] + 2.0 * vin[3]) / 5.0;
			recursiveAverage5(vin, vout, k + 1);
		} else if (k >= 2 && k < n - 2) {
			vout[k] = (vin[k - 2] + vin[k - 1] + vin[k] + vin[k + 1] + vin[k + 2]) / 5.0;
			recursiveAverage5(vin, vout, k + 1);
		} else if (k == n - 2) {
			vout[n - 2] = (vin[n - 1] + vin[n - 2] + vin[n - 3] + 2.0 * vin[n - 4]) / 5.0;
			recursiveAverage5(vin, vout, k + 1);
		} else if (k == n - 1) {
			vout[n - 1] = (vin[n - 1] + 2.0 * vin[n - 2] + 2.0 * vin[n - 3]) / 5.0;
		}
	}

	/*******************************************************************************
	 *
	 * S O B E L
	 *
	 ******************************************************************************/

	static public ImageAccess doSobel(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[3][3];
		double pixel;
		double pixel_x;
		double pixel_y;
		ImageAccess out = new ImageAccess(nx, ny);
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				pixel_x = arr[2][1] + 2*arr[2][1] + arr[2][1] - arr[0][0]- 2*arr[0][1]- arr[0][2];
				pixel_x = Math.pow(pixel_x, 2);
				pixel_y = - arr[2][0] - 2*arr[1][0] - arr[0][0] + arr[0][2] + 2*arr[1][2] + arr[2][2];
				pixel_x = Math.pow(pixel_y, 2);
				pixel = Math.sqrt(pixel_x + pixel_y);

				out.putPixel(x, y, pixel);
			}
		}
		out = rescale(out);
		return out;
	}

	/*******************************************************************************
	 *
	 * M O V I N G   A V E R A G E   L * L   S E C T I O N
	 *
	 ******************************************************************************/


	// static public ImageAccess doMovingAverageL_Recursive(ImageAccess input, int length) {
	// 	int nx = input.getWidth();
	// 	int ny = input.getHeight();
	// 	ImageAccess out = new ImageAccess(nx, ny);

	// 	int halfWindow = length / 2;
	// 	String halfWindowString = String.valueOf(halfWindow);

	// 	IJ.showMessage(halfWindowString);

	// 	recursiveAverageWindow(input, out, 0, 0, length);
		
	// 	return rescale(out);
	// }
	
	// static private void recursiveAverageWindow(ImageAccess input, ImageAccess out, int row, int col, int windowSize) {

	// 	int numRows = input.getWidth() +1;
	// 	int numCols = input.getHeight() + 1;
	// 	int halfWindow = windowSize / 2;
		
	// 	// Se todos os pixels foram processados, terminamos a recursão
	// 	if (row >= numRows) {
	// 		return;
	// 	}
	
	// 	// Processar o pixel atual (row, col)
	// 	double sum = 0.0;
	// 	int count = 0;

	// 	String rowString = String.valueOf(col);
	// 	IJ.log(rowString);
	
	// 	// Calcular a média para a janela ao redor do pixel atual
	// 	for (int i = -halfWindow; i <= halfWindow; i++) {
	// 		for (int j = -halfWindow; j <= halfWindow; j++) {
	// 			int rowIndex = row + i;
	// 			int colIndex = col + j;
	
	// 			// Espelhamento nas bordas
	// 			if (rowIndex < 0) rowIndex = -rowIndex;
	// 			if (rowIndex >= numRows) rowIndex = 2 * numRows - rowIndex - 2;
	// 			if (colIndex < 0) colIndex = -colIndex;
	// 			if (colIndex >= numCols) colIndex = 2 * numCols - colIndex - 2;
	
	// 			sum += input.getPixel(rowIndex, colIndex);
	// 			count++;
	// 		}
	// 	}
	
	// 	// Colocar o valor calculado no pixel de saída
	// 	out.putPixel(row, col, sum / count);
	
	// 	// Avançar para o próximo pixel
	// 	if (col < numCols - 1) {
	// 		// Próximo pixel na mesma linha
	// 		recursiveAverageWindow(input, out, row, col + 1, windowSize);
	// 	} else {
	// 		// Próxima linha, começar da primeira coluna
	// 		recursiveAverageWindow(input, out, row + 1, 0, windowSize);
	// 	}
	// }
	


	 static public ImageAccess doMovingAverageL_Recursive(ImageAccess input, int length) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		
		int halfWindow = length / 2;
	
		for (int row = 0; row < nx; row++) {
			for (int col = 0; col < ny; col++) {
				double sum = 0.0;
				int count = 0;
	
				for (int i = -halfWindow; i <= halfWindow; i++) {
					for (int j = -halfWindow; j <= halfWindow; j++) {
						int rowIndex = row + i;
						int colIndex = col + j;
	
						// Espelhamento nas bordas
						if (rowIndex < 0) rowIndex = -rowIndex;
						if (rowIndex >= nx) rowIndex = 2 * nx - rowIndex - 2;
						if (colIndex < 0) colIndex = -colIndex;
						if (colIndex >= ny) colIndex = 2 * ny - colIndex - 2;
	
						sum += input.getPixel(rowIndex, colIndex);
						count++;
					}
				}
				out.putPixel(row, col, sum / count);
			}
		}
	
		return rescale(out);
	}
	

    static public ImageAccess rescale(ImageAccess input) {
        int nx = input.getWidth();
        int ny = input.getHeight();
        double max = input.getMaximum();
        double min = input.getMinimum();
        double range = max - min;

        if (range == 0) {
            range = 1; // Evitar divisão por zero
        }

        ImageAccess output = new ImageAccess(nx, ny);

        for (int x = 0; x < nx; x++) {
            for (int y = 0; y < ny; y++) {
                double value = input.getPixel(x, y);
                // Normalize matrix
                value = (value - min) / range;
                // Rescale to 8 bits
                value = value * 255;
                // Arredonda e limita no intervalo [0, 255]
                value = Math.round(value);
                // Set in output pixel
                output.putPixel(x, y, value);
            }
        }
        return output;
    }
}
