package NeuralNet

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class Matrix(val rows: Int, val cols: Int)
{
    val matrix = Array(rows) { FloatArray(cols) }

    fun randomize() {
        // Set the matrix to random floats between -1 and 1
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                matrix[i][j] = Random.nextFloat() * 2f - 1f
            }
        }
    }

    fun addBias(): Matrix {
        val n = Matrix(rows + 1, 1)
        for (i in 0 until rows) {
            n.matrix[i][0] = matrix[i][0]
        }
        n.matrix[rows][0] = 1f
        return n
    }

    fun dot(n: Matrix): Matrix {
        val result = Matrix(rows, n.cols)

        if (cols == n.rows) {
            for (i in 0 until rows) {
                for (j in 0 until n.cols) {
                    var sum = 0f
                    for (k in 0 until cols) {
                        sum += matrix[i][k] * n.matrix[k][j]
                    }
                    result.matrix[i][j] = sum
                }
            }
        }
        
        return result
    }

    fun activate(): Matrix {
        val n = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                n.matrix[i][j] = sigmoid(matrix[i][j])
            }
        }
        return n
    }

    fun toArray(): FloatArray {
        val arr = FloatArray(rows * cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                arr[j + i * cols] = matrix[i][j]
            }
        }
        return arr
    }

    fun setFromArray(arr: List<Float>) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                matrix[i][j] = arr[j + i * cols]
            }
        }
    }

    // returns a matrix which has a random number of values from this matrix and the rest from the partner matrix
    fun crossover(partner: Matrix): Matrix {
        val child = Matrix(rows, cols)
        
        // pick a random point in the matrix
//        val randC = Random.nextInt(cols)
//        val randR = Random.nextInt(rows)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                child.matrix[i][j] = if (Random.nextBoolean()) matrix[i][j] else partner.matrix[i][j] 
//                child.matrix[i][j] = if ((i < randR) || (i == randR && j < randC)) {
//                    matrix[i][j]
//                } else {
//                    partner.matrix[i][j]
//                }
            }
        }
        return child
    }

    // mutation function for genetic algorithm
    fun mutate(mutationRate: Float) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val rand = Random.nextFloat()
                if (rand < mutationRate) {  // if chosen to be mutated
                    val rg = java.util.Random()
                    matrix[i][j] = max(-1F, min(1F, matrix[i][j] + rg.nextGaussian().toFloat() / 5))   // add a random value, keep between -1..1
                }
            }
        }
    }

    fun copyFrom(other: Matrix) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                matrix[i][j] = other.matrix[i][j]
            }
        }
    }

    companion object {
        fun singleColumnMatrixFromArray(inputs: FloatArray): Matrix {
            val n = Matrix(inputs.size, 1)
            for (i in inputs.indices) {
                n.matrix[i][0] = inputs[i]
            }
            return n
        }

        private fun sigmoid(x: Float): Float =
            (1 / (1 + Math.pow(Math.E, -x.toDouble()))).toFloat()
    }
}