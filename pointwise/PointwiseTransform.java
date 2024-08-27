
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


	* This method applies a contrast stretch to the input image by adjusting 
 	* the pixel values using a fixed alpha and a calculated beta.
 	*/

	static public ImageAccess rescale(ImageAccess input) { // Novo nome do método
    		int nx = input.getWidth();
    		int ny = input.getHeight();
    		double min = input.getMinimum();
    
    		double beta = min;  // Define β como o valor mínimo da imagem
    		double alpha = 1.5;  // Alpha fixo definido como 1.5
    
    		ImageAccess output = new ImageAccess(nx, ny); // Criar a imagem de saída com as mesmas dimensões
    
    		for (int x = 0; x < nx; x++) {
        		for (int y = 0; y < ny; y++) {
            		double value = input.getPixel(x, y);
            		double rescaledValue = alpha * (value - beta); // Aplicar a transformação
            
            		// Limitar o valor para o intervalo [0, 255]
            		rescaledValue = Math.max(0, Math.min(255, rescaledValue));
            
            		output.putPixel(x, y, rescaledValue); // Colocar o valor rescalado na imagem de saída
        		}
    		}
    
    		return output; // Retornar a imagem rescalada
}


	static public ImageAccess rescale(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double min = input.getMinimum();
		double max = input.getMaximum();
		
		double beta = min;
		double alpha = 255.0 / (max - min);
		
		ImageAccess output = new ImageAccess(nx, ny);
		
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				double value = input.getPixel(x, y);
				double rescaledValue = alpha * (value - beta);
				
				// Ensure the value is within [0, 255] range
				rescaledValue = Math.max(0, Math.min(255, rescaledValue));
				
				output.putPixel(x, y, rescaledValue);
			}
		}
		
		return output;    
	}

	/**
	* Question 2.3 Saturate an image
	*/
	static public ImageAccess saturate(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess output = new ImageAccess(nx, ny);

		
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				double value = input.getPixel(x, y);

			// Saturar pixels acima de 10.000
					if (value > 10000) {
					output.putPixel(x, y, 10000);
					} else {
					output.putPixel(x, y, value);
					}
			}
	   	}
	rescale(output);

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
		// Iterar sobre cada pixel na imagem de saída
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
					double maxIntensity = Double.NEGATIVE_INFINITY;
					 
					// Iterar sobre cada imagem na pilha (z-stack)
					for (int z = 0; z < nz; z++) {
						double value = zstack[z].getPixel(x, y); 
						
						// Atualizar a intensidade máxima, se o valor atual for maior
						if (value > maxIntensity) {
							maxIntensity = value;
						}
					}

					// Colocar o valor máximo encontrado na imagem de saída
					output.putPixel(x, y, maxIntensity);
			}
		}

		return output; // Retornar a imagem de projeção de intensidade máxima
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
		

		// Iterar sobre cada pixel na imagem de saída
    	for (int x = 0; x < nx; x++) {
        	for (int y = 0; y < ny; y++) {
            		double sum = 0.0; // Inicializar a soma dos valores do pixel

            		// Iterar sobre cada imagem na pilha (z-stack)
            		for (int z = 0; z < nz; z++) {
                		double value = zstack[z].getPixel(x, y);                						
						sum += value; // Somar os valores
            		}

            		// Calcular a média dos valores do pixel
           		 	double meanValue = sum / nz;

            		// Colocar o valor médio na imagem de saída
            		output.putPixel(x, y, meanValue);
       		}
    	}

		return output;	
	
	}

}
