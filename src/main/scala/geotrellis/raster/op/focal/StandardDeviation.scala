package geotrellis.raster.op.focal

import geotrellis._

import scala.math._

/**
 * Computes the standard deviation of a neighborhood for a given raster. Returns a raster of TypeDouble.
 *
 * @param    r      Raster on which to run the focal operation.
 * @param    n      Neighborhood to use for this operation (e.g., [[Square]](1))
 *
 * @note            StandardDeviation does not currently support Double raster data inputs.
 *                  If you use a Raster with a Double RasterType (TypeFloat,TypeDouble)
 *                  the data values will be rounded to integers.
 */
case class StandardDeviation(r:Op[Raster],n:Op[Neighborhood]) extends FocalOp[Raster](r,n)({
  (r,n) => new CursorCalculation[Raster] with DoubleRasterDataResult {
    var count:Int = 0
    var sum:Int = 0

    def calc(r:Raster,c:Cursor) = {
      c.removedCells.foreach { (x,y) => 
        val v = r.get(x,y)
        if(v != NODATA) { count -= 1; sum -= v } 
      }
      
      c.addedCells.foreach { (x,y) => 
        val v = r.get(x,y)
        if(v != NODATA) { count += 1; sum += v }
      }

      val mean = sum / count.toDouble
      var squares = 0.0

      c.allCells.foreach { (x,y) =>
        var v = r.get(x,y)
        if(v != NODATA) { 
          squares += math.pow(r.get(x,y) - mean,2)
        }
      }
      data.setDouble(c.col,c.row,math.sqrt(squares / count.toDouble))
    }
  }
}) with CanTile
