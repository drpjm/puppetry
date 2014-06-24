package edu.gatech.grits.puppetctrl.opt;

import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import flanagan.math.Matrix;

public class DataConverter {

	public static final XYSeriesCollection matrixToXYSeries(final Matrix output, final double stepSize, final double tInit){
		XYSeriesCollection dataCollection = new XYSeriesCollection();
		
		for(int i = 0; i < output.getNrow(); i++){
			XYSeries xy = new XYSeries("x" + (i+1));
			double t = tInit;
			for(int j = 0; j < output.getNcol(); j++){
				xy.add(t, output.getElement(i, j));
				t+=stepSize;
			}
			dataCollection.addSeries(xy);
		}
		return dataCollection;
	}
	
	public static final Matrix xySeriesToMatrix(final XYSeriesCollection xyCollection, final double stepSize, final double tInit){
		
		int nRow = xyCollection.getSeriesCount();
		int nCol = 0;
		nCol = xyCollection.getSeries(0).getItems().size();
		Matrix retMatrix = Matrix.unitMatrix(nRow, nCol);
		
		for(int i = 0; i < nRow; i++){
			for(int j = 0; j < nCol; j++){
				retMatrix.setElement(i, j, xyCollection.getSeries(i).getY(j).doubleValue());
			}
		}
		
		return retMatrix;
		
	}
	
	
}
