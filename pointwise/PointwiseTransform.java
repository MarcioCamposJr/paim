public class PointwiseTransform extends Object {

	/**
	* Question 2.1 Contrast reversal
	*/
	static public ImageAccess inverse(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess output = new ImageAccess(nx, ny);
		double value = 0.0;
		for (int x=0; x<nx; x++)
		for (int y=0; y<ny; y++) {
			value = input.getPixel(x, y);
			value = 255 - value;
			output.putPixel(x, y, value);
		}
		return output;	
	}

	/**
	* Question 2.2 Stretch normalized constrast
	*/
	static public ImageAccess rescale(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double max = input.getMaximum();
		double min = input.getMinimum();
		ImageAccess output = new ImageAccess(nx, ny);

		for (int x=0; x<nx; x++)
		for (int y=0; y<ny; y++) {
			double value = input.getPixel(x, y);
			// normalize matrix
			value  = (value - min)/(max-min);
			// rescale to  8 bits
			value = (value * 255);
			// set in output pixel
			output.putPixel(x, y, value);
		}
		return output;	
	}

	/**
	* Question 2.3 Saturate an image
	*/
	static public ImageAccess saturate(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double max = input.getMaximum() - 1;
		ImageAccess output = new ImageAccess(nx, ny);

		for (int x=0; x<nx; x++)
		for (int y=0; y<ny; y++) {

			double value = input.getPixel(x, y);
			
			// ensure not to exceed scale
			if (value > 10000){
				value = 10000;
			}

			output.putPixel(x, y, value);

		}
		output = rescale(output);
		return output;
	}
	
	/**
	* Question 4.1 Maximum Intensity Projection
	*/
	static public ImageAccess zprojectMaximum(ImageAccess[] zstack) {
		int nx = zstack[0].getWidth();
		int ny = zstack[0].getHeight();
		int nz = zstack.length;
		ImageAccess output = new ImageAccess(nx, ny);
		for(int i=0; i<nx; i++){
			for(int j=0;j<ny; j++){
				double max = 0;
				for(int k=0;k<nz;k++){
					double value = zstack[k].getPixel(i,j);
					if (value > max){
						max = value;
					}
				}
				output.putPixel(i, j, max);
			}
		}
		return output;	
	}

	/**
	* Question 4.2 Z-stack mean
	*/
	static public ImageAccess zprojectMean(ImageAccess[] zstack) {
		int nx = zstack[0].getWidth();
		int ny = zstack[0].getHeight();
		int nz = zstack.length;
		ImageAccess output = new ImageAccess(nx, ny);
		for(int i=0; i<nx; i++){
			for(int j=0;j<ny; j++){
				double sum = 0; 
				for(int k=0;k<nz;k++){
					double value = zstack[k].getPixel(i,j);
					sum = sum +value;
				}
				double mean = sum/nz;
				output.putPixel(i, j, mean);
			}
		}
		return output;	
	}

}
